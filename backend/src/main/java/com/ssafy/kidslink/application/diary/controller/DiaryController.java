package com.ssafy.kidslink.application.diary.controller;

import com.ssafy.kidslink.application.diary.dto.DiaryDTO;
import com.ssafy.kidslink.application.diary.dto.DiaryRequestDTO;
import com.ssafy.kidslink.application.diary.service.DiaryService;
import com.ssafy.kidslink.common.dto.APIResponse;
import com.ssafy.kidslink.common.exception.InvalidPrincipalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/diary")
@RequiredArgsConstructor
@Slf4j
public class DiaryController {

    private final DiaryService diaryService;

    @PostMapping("/child/{childId}")
    public ResponseEntity<APIResponse<Void>> createDiary(@AuthenticationPrincipal Object principal, @PathVariable("childId") int childId, @ModelAttribute DiaryRequestDTO request) {
        if(principal instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) principal;
            String teacherUsername = userDetails.getUsername();
            diaryService.createDiary(childId, teacherUsername, request);
            APIResponse<Void> responseData = new APIResponse<>(
                    "success",
                    null,
                    "성장일지가 성공적으로 작성되었습니다.",
                    null
            );
            return new ResponseEntity<>(responseData, HttpStatus.OK);
        }
        throw new InvalidPrincipalException("Invalid principal type.");
    }

    @GetMapping("/child/{childId}")
    public ResponseEntity<APIResponse<List<DiaryDTO>>> getAllDiary(@AuthenticationPrincipal Object principal, @PathVariable("childId") int childId) {
        if(principal instanceof UserDetails) {
            List<DiaryDTO> diaries = diaryService.getAllDiary(childId);
            APIResponse<List<DiaryDTO>> responseData = new APIResponse<>(
                    "success",
                    diaries,
                    "성장일지를 성공적으로 조회했습니다.",
                    null
            );
            return new ResponseEntity<>(responseData, HttpStatus.OK);
        }
        throw new InvalidPrincipalException("Invalid principal type.");
    }

    @GetMapping("{diaryId}")
    public ResponseEntity<APIResponse<DiaryDTO>> getDiary(@AuthenticationPrincipal Object principal, @PathVariable("diaryId") int diaryId) {
        if(principal instanceof UserDetails) {
            DiaryDTO diary = diaryService.getDiary(diaryId);

            APIResponse<DiaryDTO> responseData = new APIResponse<>(
                    "success",
                    diary,
                    "성장일지를 성공적으로 조회했습니다.",
                    null
            );
            return new ResponseEntity<>(responseData, HttpStatus.OK);
        }
        throw new InvalidPrincipalException("Invalid principal type.");
    }

}
