package rs.uns.ftn.clouddbadapter.core;

public enum Provider {
    AWS, GCP, AZURE;

    public static Provider parse(String s) {
        if (s == null) return AWS;
        return switch (s.trim().toLowerCase()) {
            case "aws" -> AWS;
            case "gcp" -> GCP;
            case "azure" -> AZURE;
            default -> throw new IllegalArgumentException("Unknown provider: " + s);
        };
    }
}
