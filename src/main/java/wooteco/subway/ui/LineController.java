package wooteco.subway.ui;

import java.net.URI;
import java.util.List;
import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.service.LineService;
import wooteco.subway.service.SectionService;

@RestController
@RequestMapping("/lines")
public class LineController {

    private final LineService lineService;

    private final SectionService sectionService;

    public LineController(final LineService lineService, final SectionService sectionService) {
        this.lineService = lineService;
        this.sectionService = sectionService;
    }

    @PostMapping
    public ResponseEntity<LineResponse> createLine(@RequestBody @Valid final LineRequest lineRequest) {
        final LineResponse lineResponse = lineService.create(lineRequest);

        return ResponseEntity.created(URI.create("/lines/" + lineResponse.getId())).body(lineResponse);
    }

    @GetMapping
    public List<LineResponse> getAllLines() {
        return lineService.getAll();
    }

    @GetMapping("/{id}")
    public LineResponse getLineById(@PathVariable final Long id) {
        return lineService.getById(id);
    }

    @PutMapping("/{id}")
    public void updateLine(@PathVariable final Long id, @RequestBody @Valid final LineRequest lineRequest) {
        lineService.modify(id, lineRequest);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLine(@PathVariable final Long id) {
        lineService.remove(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/sections")
    public void addSection(@PathVariable final Long id, @RequestBody @Valid final SectionRequest sectionRequest) {
        sectionService.create(id, sectionRequest);
    }

    @DeleteMapping("/{id}/sections")
    public void deleteSection(@PathVariable final Long id, @RequestParam final Long stationId) {
        sectionService.remove(id, stationId);
    }
}
