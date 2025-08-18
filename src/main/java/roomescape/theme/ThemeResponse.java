package roomescape.theme;

public record ThemeResponse(Long id, String name, String description) {

    public static ThemeResponse from(Theme theme) {
        return new ThemeResponse(theme.getId(), theme.getName(), theme.getDescription());
    }
}
