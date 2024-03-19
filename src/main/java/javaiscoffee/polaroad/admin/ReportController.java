package javaiscoffee.polaroad.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import javaiscoffee.polaroad.response.ResponseMessages;
import javaiscoffee.polaroad.security.CustomUserDetails;
import javaiscoffee.polaroad.wrapper.RequestWrapperDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/report")
@RequiredArgsConstructor
@Tag(name = "신고 기능 관련 모음",description = "신고 생성&조회&삭제 api 모음 - 담당자 박상현")
public class ReportController {
    private final ReportService reportService;

    @Operation(summary = "멤버가 포스트나 리뷰 신고하기", description = "신고 생성하는 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "신고에 성공했을 경우"),
            @ApiResponse(responseCode = "400", description = "같은 대상에 대해 중복 신고를 할 경우"),
            @ApiResponse(responseCode = "404", description = "멤버나 대상 포스트가 존재하지 않는 경우")
    })
    @PostMapping("/write")
    public ResponseEntity<String> writeReport (@AuthenticationPrincipal CustomUserDetails userDetails,
                                               @RequestBody RequestWrapperDto<ReportSaveDto> wrapperDto) {
        Long memberId = userDetails.getMemberId();
        ReportSaveDto saveDto = wrapperDto.getData();
        reportService.saveReport(saveDto, memberId);
        return ResponseEntity.ok(ResponseMessages.SUCCESS.getMessage());
    }

    @Operation(summary = "관리자가 신고 목록 조회 api", description = "신고항목들을 처리 상태에 따라 조회하는 api \n ## 처리 상태를 null로 두면 처리상태 상관 없이 전체 조회")
    @Parameter(name = "page", description = "0부터 시작합니다. 몇 번째 페이지를 출력할 것인지",required = true, example = "0")
    @Parameter(name = "pageSize", description = "한 페이지에 몇 개의 결과를 표시할 것인지 정하는 수치", required = true, example = "8")
    @Parameter(name = "status", description = "조회할 신고 처리 상태 설정 \n null로 두면 전체 조회", required = true, example = "INCOMPLETE")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "신고 조회에 성공했을 경우"),
            @ApiResponse(responseCode = "403", description = "멤버가 관리자가 아닌 경우"),
            @ApiResponse(responseCode = "404", description = "멤버가 존재하지 않는 경우")
    })
    @GetMapping("/list")
    public ResponseEntity<ReportListDto> getReportList(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                       @RequestParam(name = "page") int page,
                                                       @RequestParam(name = "pageSize") int pageSize,
                                                       @RequestParam(name = "status") ReportStatus status) {
        Long memberId = userDetails.getMemberId();
        return ResponseEntity.ok(reportService.getReportList(memberId,page,pageSize,status));
    }

    @Operation(summary = "신고 대상 게시글 | 리뷰 삭제 api", description = "관리자가 신고당한 게시글이나 댓글 삭제하는 api")
    @Parameter(name = "reportId", description = "대상 게시글이나 리뷰 삭제할 신고 ID",required = true, example = "0")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "신고 항목 삭제에 성공했을 경우"),
            @ApiResponse(responseCode = "403", description = "멤버가 관리자가 아닌 경우"),
            @ApiResponse(responseCode = "404", description = "멤버나 신고, 삭제할 대상이 존재하지 않는 경우")
    })
    @DeleteMapping("/target/delete/{reportId}")
    public ResponseEntity<String> deleteReportTarget(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                     @PathVariable(name = "reportId") Long reportId) {
        Long memberId = userDetails.getMemberId();
        reportService.deleteReportTarget(memberId, reportId);
        return ResponseEntity.ok(ResponseMessages.SUCCESS.getMessage());
    }
}
