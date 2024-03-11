package javaiscoffee.polaroad.test;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 크램폴린에서 처음 실행될 때 자동 api테스트에 사용되는 컨트롤러
 */

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
@Hidden
public class TestController {

    private final SampleDataRepository sampleDataRepository;
    @GetMapping("/test")
    public ResponseEntity<?> pingTest() {
        return ResponseEntity.ok(true);
    }

    @GetMapping("/db")
    public ResponseEntity<?> dbTest() {
        List<SampleData> sampleDataList = sampleDataRepository.findAll();
        return ResponseEntity.ok(sampleDataList);
    }
}
