package com.objects.marketbridge.member.controller;

import com.objects.marketbridge.common.config.KakaoPayConfig;
import com.objects.marketbridge.common.dto.KakaoPayApproveResponse;
import com.objects.marketbridge.common.dto.KakaoPayReadyRequest;
import com.objects.marketbridge.common.dto.KakaoPayReadyResponse;
import com.objects.marketbridge.common.interceptor.ApiResponse;
import com.objects.marketbridge.common.security.annotation.AuthMemberId;
import com.objects.marketbridge.member.controller.request.CreateSubsRequest;
import com.objects.marketbridge.member.dto.CreateSubsDto;
import com.objects.marketbridge.member.service.MemberShipService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static com.objects.marketbridge.common.config.KakaoPayConfig.SUBS_CID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/membership")
public class MemberShipController {

    private final MemberShipService memberShipService;
    private final KakaoPayConfig kakaoPayConfig;

    //TODO uri명명
    @PostMapping("/subsMember")
    public ApiResponse<KakaoPayReadyResponse> saveOrder( // 받을때 cid를 정기결제용
                                                         @AuthMemberId Long memberId,
                                                         @Valid @RequestBody CreateSubsRequest request) {
        String subsOrderNo = UUID.randomUUID().toString();
        // 1. kakaoSubsPaymentReadyService 호출
        KakaoPayReadyResponse response = memberShipService.kakaoPayReady(createKakaoReadyRequest(request, memberId,subsOrderNo));

        // 2. 정보 저장
        memberShipService.savePayReadyData(createSubsDto(request.getPrice(), memberId, response.getTid(),subsOrderNo));

        return ApiResponse.ok(response);
    }


    @GetMapping("/kakao-pay/approval/{orderNo}")
    public ApiResponse<KakaoPayApproveResponse> kakaoPaymentApproved(
            @RequestParam(name = "pg_token") String pgToken,
            @PathVariable (name = "orderNo") String orderNo){

        //TODO 성능최적화에 필요한 멤버랑 멤버십 fetchjoin으로 가져오는 JPQL 쿼리메서드 필요
        KakaoPayApproveResponse response = memberShipService.kakaoPayApprove(pgToken,orderNo);

        // 2. Payment 생성 및 OrderDetails 업데이트
        memberShipService.saveApprovalResponse(response);
        memberShipService.changeMemberShip(Long.parseLong(response.getPartnerUserId()));
        return ApiResponse.ok(response);
    }

    //정기결제 2회차
    @PostMapping("/subscription")
    public ApiResponse<KakaoPayApproveResponse> kakaoPaySubsPayment() {
        //TODO 배치로 DB에서 필요한 값을 받아서 처리를 해야함

        KakaoPayApproveResponse response = memberShipService.kakaoPaySubsApprove();
        memberShipService.saveApprovalResponse(response);
        memberShipService.changeMemberShip(Long.parseLong(response.getPartnerUserId()));
        return ApiResponse.ok(response);
    }

    private KakaoPayReadyRequest createKakaoReadyRequest(CreateSubsRequest request, Long memberId, String subsOrderNo) {
        String cid = SUBS_CID;
        String cancelUrl = kakaoPayConfig.getRedirectCancelUrl();
        String failUrl = kakaoPayConfig.getRedirectFailUrl();
        String approvalUrl = kakaoPayConfig.createApprovalUrl("/membership");

        log.info("cancelUrl , {}" ,cancelUrl);
        log.info("failUrl , {}" ,failUrl);
        log.info("approvalUrl , {}" ,approvalUrl);
        return request.toKakaoReadyRequest(memberId,subsOrderNo, cid, approvalUrl, failUrl, cancelUrl);
    }

    // 정기결제 Request 생성 메서드
    private CreateSubsDto createSubsDto(Long price, Long memberId, String tid, String subsOrderNo) {
        return CreateSubsDto.of(price, memberId, tid ,subsOrderNo);
    }
}
