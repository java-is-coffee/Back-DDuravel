package javaiscoffee.polaroad.redis;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/redis")
@Tag(name = "레디스 관련 API",description = "참고용으로 만든 레디스 API - 담당자 박상현")
public class RedisController {

    private final RedisService redisService;

    @Autowired
    public RedisController(RedisService redisService) {
        this.redisService = redisService;
    }

    @PostMapping("/set")
    public String setValue(@RequestParam String key, @RequestParam String value) {
        redisService.setValue(key, value);
        return "Value set";
    }

    @GetMapping("/get/{key}")
    public String getValue(@PathVariable String key) {
        return redisService.getValue(key);
    }
}

