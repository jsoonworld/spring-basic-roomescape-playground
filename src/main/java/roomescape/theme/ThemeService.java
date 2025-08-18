package roomescape.theme;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ThemeService {
    private final ThemeRepository themeRepository;

    public ThemeService(ThemeRepository themeRepository) {
        this.themeRepository = themeRepository;
    }

    public ThemeResponse create(ThemeRequest request) {
        Theme newTheme = new Theme(request.name(), request.description());
        Theme savedTheme = themeRepository.save(newTheme);
        return ThemeResponse.from(savedTheme);
    }

    public List<ThemeResponse> findAll() {
        return themeRepository.findAll()
                .stream()
                .map(ThemeResponse::from)
                .collect(Collectors.toList());
    }

    public void delete(Long id) {
        themeRepository.deleteById(id);
    }
}
