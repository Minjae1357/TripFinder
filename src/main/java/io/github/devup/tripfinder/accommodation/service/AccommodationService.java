package io.github.devup.tripfinder.accommodation.service;

import io.github.devup.tripfinder.accommodation.dto.response.AccommodationDetailResponse;
import io.github.devup.tripfinder.accommodation.dto.response.AccommodationListResponse;
import io.github.devup.tripfinder.accommodation.dto.response.RoomResponse;
import io.github.devup.tripfinder.accommodation.entity.Accommodation;
import io.github.devup.tripfinder.accommodation.entity.Room;
import io.github.devup.tripfinder.accommodation.entity.RoomImg;
import io.github.devup.tripfinder.accommodation.exception.AccommodationNotFoundException;
import io.github.devup.tripfinder.accommodation.repository.AccommodationRepository;
import io.github.devup.tripfinder.accommodation.repository.RoomImgRepository;
import io.github.devup.tripfinder.accommodation.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service

@RequiredArgsConstructor
public class AccommodationService {
    private final AccommodationRepository accommodationRepository;
    private final RoomRepository roomRepository;
    private final RoomImgRepository roomImgRepository;

    @Transactional(readOnly = true)
    public List<AccommodationListResponse> getAccommodationList() {
        return accommodationRepository.findAll().stream()
                .map(accommodationList -> new AccommodationListResponse(
                        accommodationList.getAccommodationId(),
                        accommodationList.getAccommodationName(),
                        accommodationList.getAccommodationType(),
                        accommodationList.getAccommodationLat(),
                        accommodationList.getAccommodationLng()
                ))
                .toList();
    }
    // 상세 조회
    @Transactional(readOnly = true)
    public AccommodationDetailResponse getAccommodationDetail(Long accommodationId) {
        Accommodation accommodation = accommodationRepository.findById(accommodationId).orElseThrow(()-> new AccommodationNotFoundException("숙소를 찾을 수 없습니다."));

        List<Room> rooms = roomRepository.findByAccommodation_AccommodationId(accommodationId);

        List<RoomResponse> roomResponses = rooms.stream().map(this::toRoomResponse).toList();

        return new AccommodationDetailResponse(
                accommodation.getAccommodationId(),
                accommodation.getAccommodationName(),
                accommodation.getAccommodationType(),
                accommodation.getRegion(),
                accommodation.getAddress(),
                accommodation.getAccommodationLat(),
                accommodation.getAccommodationLng(),
                roomResponses
        );
    }

    private RoomResponse toRoomResponse(Room room){
        List<String> imgUrls = roomImgRepository.findByRoom_RoomIdOrderByImgOrder(room.getRoomId()).stream().map(RoomImg::getImgUrl).toList();
        return new RoomResponse(
                room.getRoomId(),
                room.getRoomName(),
                room.getPrice(),
                room.getMaxGuest(),
                room.getTotalRoomCount(),
                room.getHasSpa(),
                room.getContents(),
                imgUrls
        );
    }

}
