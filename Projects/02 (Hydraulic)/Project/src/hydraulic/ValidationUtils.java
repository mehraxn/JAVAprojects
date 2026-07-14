package hydraulic;

final class ValidationUtils {
    private static final double PROPORTION_TOLERANCE = 1.0e-9;

    private ValidationUtils() {
    }

    static String requireNotBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " cannot be null or blank");
        }
        return value;
    }

    static double requireNonNegative(double value, String fieldName) {
        if (Double.isNaN(value) || value < 0.0) {
            throw new IllegalArgumentException(fieldName + " must be non-negative");
        }
        return value;
    }

    static int requirePositive(int value, String fieldName) {
        if (value <= 0) {
            throw new IllegalArgumentException(fieldName + " must be positive");
        }
        return value;
    }

    static void requireOutputIndex(int index, int outputCount) {
        if (index < 0 || index >= outputCount) {
            throw new IllegalArgumentException(
                    "output index must be between 0 and " + (outputCount - 1));
        }
    }

    static double[] requireProportions(double[] proportions, int outputCount) {
        if (proportions == null) {
            throw new IllegalArgumentException("proportions cannot be null");
        }
        if (proportions.length != outputCount) {
            throw new IllegalArgumentException(
                    "expected " + outputCount + " proportions but got " + proportions.length);
        }

        double sum = 0.0;
        for (double proportion : proportions) {
            if (!Double.isFinite(proportion) || proportion < 0.0) {
                throw new IllegalArgumentException("proportions must be finite and non-negative");
            }
            sum += proportion;
        }
        if (Math.abs(sum - 1.0) > PROPORTION_TOLERANCE) {
            throw new IllegalArgumentException("proportions must sum to 1.0");
        }
        return proportions.clone();
    }
}
