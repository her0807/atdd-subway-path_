package wooteco.subway.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wooteco.subway.controller.dto.ControllerDtoAssembler;
import wooteco.subway.controller.dto.path.PathRequest;
import wooteco.subway.controller.dto.path.PathResponse;
import wooteco.subway.service.PathService;

@RestController
@RequestMapping("/paths")
public class PathController {

    private final PathService pathService;

    public PathController(PathService pathService) {
        this.pathService = pathService;
    }

    @GetMapping
    public ResponseEntity<PathResponse> findPath(PathRequest pathRequest) {
        PathResponse pathResponse = ControllerDtoAssembler.pathResponse(pathService.getPath(ControllerDtoAssembler.pathRequestDto(pathRequest)));
        return ResponseEntity.ok().body(pathResponse);
    }
}
