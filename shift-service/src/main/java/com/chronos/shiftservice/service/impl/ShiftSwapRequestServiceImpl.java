package com.chronos.shiftservice.service.impl;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.chronos.common.constants.ErrorConstants;
import com.chronos.common.constants.UuidErrorConstants;
import com.chronos.common.constants.enums.Role;
import com.chronos.common.constants.enums.ShiftStatus;
import com.chronos.common.constants.enums.ShiftSwapRequestStatus;
import com.chronos.common.exception.custom.ResourceNotFoundException;
import com.chronos.common.exception.custom.ShiftSwapRequestException;
import com.chronos.common.util.NanoIdGenerator;
import com.chronos.shiftservice.dto.EmployeeDTO;
import com.chronos.shiftservice.dto.shiftSwapRequest.CreateShiftSwapRequestDTO;
import com.chronos.shiftservice.dto.shiftSwapRequest.ShiftSwapQueryResponseDTO;
import com.chronos.shiftservice.dto.shiftSwapRequest.ShiftSwapResponseDTO;
import com.chronos.shiftservice.entity.Shift;
import com.chronos.shiftservice.entity.ShiftSwapRequest;
import com.chronos.shiftservice.feign.EmployeeClient;
import com.chronos.shiftservice.repository.ShiftRepository;
import com.chronos.shiftservice.repository.ShiftSwapRepository;
import com.chronos.shiftservice.service.ShiftSwapRequestService;
import com.chronos.shiftservice.utils.mappers.ShiftSwapMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.chronos.common.util.ParseUUID.parseUUID;


@Service
public class ShiftSwapRequestServiceImpl implements ShiftSwapRequestService {
    private final ShiftSwapRepository shiftSwapRepository;
    private final ShiftRepository shiftRepository;
    private final EmployeeClient employeeClient;

    @Autowired
    public ShiftSwapRequestServiceImpl(
            ShiftSwapRepository shiftSwapRepository,
            ShiftRepository shiftRepository,
            EmployeeClient employeeClient) {
        this.shiftSwapRepository = shiftSwapRepository;
        this.shiftRepository = shiftRepository;
        this.employeeClient = employeeClient;
    }

    @Override
    @Transactional
    public ShiftSwapResponseDTO createSwapRequest(CreateShiftSwapRequestDTO createSwapDto) {
        if (createSwapDto.requesterEmployeeId() == null) {
            throw new ShiftSwapRequestException(ErrorConstants.REQUESTER_NOT_FOUND);
        }

        if (createSwapDto.requestedEmployeeId() == null) {
            throw new ShiftSwapRequestException(ErrorConstants.REQUESTED_NOT_FOUND);
        }

        UUID requesterId = createSwapDto.requesterEmployeeId();
        UUID requestedId = createSwapDto.requestedEmployeeId();


        if (requesterId.equals(requestedId)) {
            throw new ResourceNotFoundException(ErrorConstants.DIFF_REQUESTER_REQUESTED_EMP);
        }

        if (createSwapDto.offeringShiftId() == null || createSwapDto.requestingShiftId() == null) {
            throw new ShiftSwapRequestException(ErrorConstants.ALL_IDS_REQUIRED);
        }

        EmployeeDTO requester = employeeClient.getEmployeeById(requesterId.toString());

        EmployeeDTO requested = employeeClient.getEmployeeById(requestedId.toString());

        if (requester.teamId() == null || requested.teamId() == null || !requester.teamId().equals(requested.teamId())) {
            throw new ShiftSwapRequestException(ErrorConstants.EMPLOYEE_NOT_IN_MANAGER_TEAM);
        }

        if (requested.role() == Role.MANAGER) {
            throw new ShiftSwapRequestException(ErrorConstants.CANNOT_SWAP_WITH_MANAGER);
        }

        Shift offeringShift = shiftRepository.findById(createSwapDto.offeringShiftId())
                .orElseThrow(() -> new ShiftSwapRequestException(ErrorConstants.OFFERING_SHIFT_NOT_FOUND));

        Shift requestingShift = shiftRepository.findById(createSwapDto.requestingShiftId())
                .orElseThrow(() -> new ShiftSwapRequestException(ErrorConstants.REQUESTING_SHIFT_NOT_FOUND));


        if (!offeringShift.getEmployeeId().equals(requesterId)) {
            throw new ShiftSwapRequestException(ErrorConstants.INVALID_OFFERING_SHIFT);
        }

        if (!requestingShift.getEmployeeId().equals(requestedId)) {
            throw new ShiftSwapRequestException(ErrorConstants.INVALID_REQUESTING_SHIFT);
        }


        var now = OffsetDateTime.now();
        if (offeringShift.getShiftStartTime().isBefore(now) || requestingShift.getShiftStartTime().isBefore(now)) {
            throw new ShiftSwapRequestException(ErrorConstants.STARTED_SHIFT_SWAP_ERROR);
        }


        int shiftSwapIdLength = 10;
        String nanoId = NanoIdUtils.randomNanoId(
                NanoIdGenerator.DEFAULT_NUMBER_GENERATOR,
                NanoIdGenerator.DEFAULT_ALPHABET,
                shiftSwapIdLength
        );

        offeringShift.setShiftStatus(ShiftStatus.PENDING);
        requestingShift.setShiftStatus(ShiftStatus.PENDING);
        shiftRepository.save(offeringShift);
        shiftRepository.save(requestingShift);

        ShiftSwapRequest shiftSwapEntity = new ShiftSwapRequest();

        shiftSwapEntity.setPublicId("SSR-" + nanoId);
        shiftSwapEntity.setRequesterEmployeeId(requesterId);
        shiftSwapEntity.setRequestedEmployeeId(requestedId);
        shiftSwapEntity.setOfferingShift(offeringShift);
        shiftSwapEntity.setRequestingShift(requestingShift);
        shiftSwapEntity.setStatus(ShiftSwapRequestStatus.PENDING);
        shiftSwapEntity.setReason(createSwapDto.reason());
        shiftSwapEntity.setApprovedBy(null);
        shiftSwapEntity.setApprovedDate(null);

        ShiftSwapRequest savedSwap = shiftSwapRepository.save(shiftSwapEntity);

        String requesterName = buildName(requester);
        String requestedName = buildName(requested);

        return ShiftSwapMapper.shiftSwapEntityToDto(savedSwap, requesterName, requestedName, null);
    }


    @Override
    public List<ShiftSwapQueryResponseDTO> getSwapRequestsForEmployee(String employeeId) {
        UUID requesterOrRequestedID = parseUUID(employeeId, UuidErrorConstants.INVALID_REQUESTER_OR_REQUESTED_ID);


        List<ShiftSwapRequest> list = shiftSwapRepository.findSwapRequestsByEmployee(requesterOrRequestedID);

        Map<UUID, String> nameCache = new HashMap<>();

        return list.stream().map(e -> {
            String requesterName = nameCache.computeIfAbsent(e.getRequesterEmployeeId(),
                    id -> buildName(employeeClient.getEmployeeById(id.toString())));
            String requestedName = nameCache.computeIfAbsent(e.getRequestedEmployeeId(),
                    id -> buildName(employeeClient.getEmployeeById(id.toString())));
            String approvedByName = e.getApprovedBy() == null ? null : nameCache.computeIfAbsent(e.getApprovedBy(), id -> buildName(employeeClient.getEmployeeById(id.toString())));

            return ShiftSwapMapper.toQueryDto(e, requesterName, requestedName, approvedByName);
        }).toList();
    }

    @Override
    public List<ShiftSwapQueryResponseDTO> getTeamSwapRequests(String managerId) {
        List<EmployeeDTO> team = employeeClient.getTeamMembers(managerId);
        List<UUID> empIds = team.stream().map(EmployeeDTO::id).toList();

        if (empIds.isEmpty()) {
            return List.of();
        }


        List<ShiftSwapRequest> list = shiftSwapRepository.findTeamSwapRequests(empIds);

        Map<UUID, String> nameCache = new HashMap<>();

        return list.stream().map(e -> {
            String requesterName = nameCache.computeIfAbsent(e.getRequesterEmployeeId(),
                    id -> buildName(employeeClient.getEmployeeById(id.toString())));
            String requestedName = nameCache.computeIfAbsent(e.getRequestedEmployeeId(),
                    id -> buildName(employeeClient.getEmployeeById(id.toString())));
            String approvedByName = e.getApprovedBy() == null ? null : nameCache.computeIfAbsent(e.getApprovedBy(), id -> buildName(employeeClient.getEmployeeById(id.toString())));

            return ShiftSwapMapper.toQueryDto(e, requesterName, requestedName, approvedByName);
        }).toList();

    }

    @Override
    @Transactional
    public ShiftSwapResponseDTO approveSwapRequest(String managerId, String swapRequestId) {
        UUID swapReqID = parseUUID(swapRequestId, UuidErrorConstants.INVALID_SWAP_REQUEST_ID);

        ShiftSwapRequest shiftSwapRequest = shiftSwapRepository.findById(swapReqID)
                .orElseThrow(() -> new ShiftSwapRequestException(ErrorConstants.SWAP_REQUEST_NOT_FOUND));

        if (shiftSwapRequest.getStatus() != ShiftSwapRequestStatus.PENDING) {
            throw new ShiftSwapRequestException(ErrorConstants.PENDING_REQUEST_HANDLE_ONLY);
        }

        Set<UUID> teamIds = employeeClient.getTeamMembers(managerId).stream().map(EmployeeDTO::id)
                .collect(Collectors.toSet());
        if (!teamIds.contains(shiftSwapRequest.getRequesterEmployeeId()) || !teamIds.contains(shiftSwapRequest.getRequestedEmployeeId())) {
            throw new IllegalStateException(ErrorConstants.MANAGER_WITH_NO_TEAM);
        }

        // get the offering and requesting shift
        Shift offeringShift = shiftSwapRequest.getOfferingShift();
        Shift requestingShift = shiftSwapRequest.getRequestingShift();

        // then just get the employees from the request
        UUID requesterId = shiftSwapRequest.getRequesterEmployeeId();
        UUID requestedId = shiftSwapRequest.getRequestedEmployeeId();

        // after that just swap the requests
        offeringShift.setEmployeeId(requestedId);
        requestingShift.setEmployeeId(requesterId);
        offeringShift.setShiftStatus(ShiftStatus.CONFIRMED);
        requestingShift.setShiftStatus(ShiftStatus.CONFIRMED);

        // finally save that using the repo
        shiftRepository.save(offeringShift);
        shiftRepository.save(requestingShift);


        // .. then just mark that swap request as approved by setting the required values
        shiftSwapRequest.setStatus(ShiftSwapRequestStatus.APPROVED);
        shiftSwapRequest.setApprovedBy(parseUUID(managerId, UuidErrorConstants.INVALID_MANAGER_UUID));
        shiftSwapRequest.setApprovedDate(OffsetDateTime.now());

        ShiftSwapRequest savedSwap = shiftSwapRepository.save(shiftSwapRequest);

        String requesterName = buildName(employeeClient.getEmployeeById(requesterId.toString()));
        String requestedName = buildName(employeeClient.getEmployeeById(requestedId.toString()));
        String approvedByName = buildName(employeeClient.getEmployeeById(shiftSwapRequest.getApprovedBy().toString()));


        return ShiftSwapMapper.shiftSwapEntityToDto(savedSwap, requesterName, requestedName, approvedByName);
    }

    @Override
    @Transactional
    public ShiftSwapResponseDTO rejectSwapRequest(String managerId, String swapRequestId) {
        UUID swapReqID = parseUUID(swapRequestId, UuidErrorConstants.INVALID_SWAP_REQUEST_ID);

        ShiftSwapRequest shiftSwapRequest = shiftSwapRepository.findById(swapReqID)
                .orElseThrow(() -> new ShiftSwapRequestException(ErrorConstants.SWAP_REQUEST_NOT_FOUND));

        if (shiftSwapRequest.getStatus() != ShiftSwapRequestStatus.PENDING) {
            throw new ShiftSwapRequestException(ErrorConstants.PENDING_REQUEST_HANDLE_ONLY);
        }

        Set<UUID> teamIds = employeeClient.getTeamMembers(managerId).stream().map(EmployeeDTO::id)
                .collect(Collectors.toSet());
        if (!teamIds.contains(shiftSwapRequest.getRequesterEmployeeId()) || !teamIds.contains(shiftSwapRequest.getRequestedEmployeeId())) {
            throw new ResourceNotFoundException(ErrorConstants.MANAGER_WITH_NO_TEAM);
        }

        // just only mark the swap request as rejected
        // don't have to do anything in the shift repo
        Shift offeringShift = shiftSwapRequest.getOfferingShift();
        Shift requestingShift = shiftSwapRequest.getRequestingShift();


        // reason: if rejected i want to keep the original shifts as it is
        // otherwise the employee might think it as his/her shift is cancelled and that is a holiday
        // for that keeping the status to Confirmed will make sense rather than rejecting it
        offeringShift.setShiftStatus(ShiftStatus.CONFIRMED);
        requestingShift.setShiftStatus(ShiftStatus.CONFIRMED);
        shiftRepository.save(offeringShift);
        shiftRepository.save(requestingShift);


        shiftSwapRequest.setStatus(ShiftSwapRequestStatus.REJECTED);
        shiftSwapRequest.setApprovedBy(parseUUID(managerId, UuidErrorConstants.INVALID_MANAGER_UUID));
        shiftSwapRequest.setApprovedDate(OffsetDateTime.now());

        ShiftSwapRequest savedSwap = shiftSwapRepository.save(shiftSwapRequest);

        String requesterName = buildName(employeeClient.getEmployeeById(shiftSwapRequest.getRequesterEmployeeId().toString()));
        String requestedName = buildName(employeeClient.getEmployeeById(shiftSwapRequest.getRequestedEmployeeId().toString()));
        String approvedByName = buildName(employeeClient.getEmployeeById(shiftSwapRequest.getApprovedBy().toString()));

        return ShiftSwapMapper.shiftSwapEntityToDto(savedSwap, requesterName, requestedName, approvedByName);
    }


    private String buildName(EmployeeDTO e) {
        if (e == null) return "";
        String lName = e.lastName();
        return (lName == null || lName.isBlank()) ? e.firstName() : e.firstName() + " " + lName;
    }
}
