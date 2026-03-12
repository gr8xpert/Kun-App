package com.example.kunworld.utils;

import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * AbjadCalculator - Implements Abjad numerology calculations for Istikhara features.
 *
 * Abjad numerals are a decimal alphabetic numeral system/alphanumeric code,
 * where the 28 letters of the Arabic alphabet are assigned numerical values.
 */
public class AbjadCalculator {

    // Arabic/Urdu letter values (Abjad numerology)
    private static final Map<Character, Integer> LETTER_VALUES = new HashMap<>();

    static {
        // Alif variants - value 1
        LETTER_VALUES.put('\u0627', 1);  // ا (Alif)
        LETTER_VALUES.put('\u0623', 1);  // أ (Alif with Hamza above)
        LETTER_VALUES.put('\u0625', 1);  // إ (Alif with Hamza below)
        LETTER_VALUES.put('\u0622', 1);  // آ (Alif with Madda)

        // Ba, Pa - value 2
        LETTER_VALUES.put('\u0628', 2);  // ب (Ba)
        LETTER_VALUES.put('\u067E', 2);  // پ (Pa - Urdu)

        // Jeem, Cheem - value 3
        LETTER_VALUES.put('\u062C', 3);  // ج (Jeem)
        LETTER_VALUES.put('\u0686', 3);  // چ (Cheem - Urdu)

        // Dal - value 4
        LETTER_VALUES.put('\u062F', 4);  // د (Dal)

        // Ha variants - value 5
        LETTER_VALUES.put('\u0647', 5);  // ه (Ha)
        LETTER_VALUES.put('\u06C1', 5);  // ہ (Ha - Urdu form)
        LETTER_VALUES.put('\u0629', 5);  // ة (Ta Marbuta)

        // Waw - value 6
        LETTER_VALUES.put('\u0648', 6);  // و (Waw)

        // Zay, Zhay - value 7
        LETTER_VALUES.put('\u0632', 7);  // ز (Zay)
        LETTER_VALUES.put('\u0698', 7);  // ژ (Zhay - Persian/Urdu)

        // Ha (Haa) - value 8
        LETTER_VALUES.put('\u062D', 8);  // ح (Haa)

        // Ta - value 9
        LETTER_VALUES.put('\u0637', 9);  // ط (Ta)

        // Ya variants - value 10
        LETTER_VALUES.put('\u064A', 10); // ي (Ya - Arabic)
        LETTER_VALUES.put('\u06CC', 10); // ی (Ya - Urdu/Persian)
        LETTER_VALUES.put('\u0649', 10); // ى (Alif Maksura)
        LETTER_VALUES.put('\u06D2', 10); // ے (Bari Ye - Urdu)

        // Kaf, Gaf - value 20
        LETTER_VALUES.put('\u0643', 20); // ك (Kaf - Arabic)
        LETTER_VALUES.put('\u06A9', 20); // ک (Kaf - Urdu/Persian)
        LETTER_VALUES.put('\u06AF', 20); // گ (Gaf - Urdu/Persian)

        // Lam - value 30
        LETTER_VALUES.put('\u0644', 30); // ل (Lam)

        // Meem - value 40
        LETTER_VALUES.put('\u0645', 40); // م (Meem)

        // Noon - value 50
        LETTER_VALUES.put('\u0646', 50); // ن (Noon)

        // Seen - value 60
        LETTER_VALUES.put('\u0633', 60); // س (Seen)

        // Ain - value 70
        LETTER_VALUES.put('\u0639', 70); // ع (Ain)

        // Fa - value 80
        LETTER_VALUES.put('\u0641', 80); // ف (Fa)

        // Sad - value 90
        LETTER_VALUES.put('\u0635', 90); // ص (Sad)

        // Qaf - value 100
        LETTER_VALUES.put('\u0642', 100); // ق (Qaf)

        // Ra - value 200
        LETTER_VALUES.put('\u0631', 200); // ر (Ra)

        // Sheen - value 300
        LETTER_VALUES.put('\u0634', 300); // ش (Sheen)

        // Ta - value 400
        LETTER_VALUES.put('\u062A', 400); // ت (Ta)

        // Tha - value 500
        LETTER_VALUES.put('\u062B', 500); // ث (Tha)

        // Kha - value 600
        LETTER_VALUES.put('\u062E', 600); // خ (Kha)

        // Thal - value 700
        LETTER_VALUES.put('\u0630', 700); // ذ (Thal)

        // Dad - value 800
        LETTER_VALUES.put('\u0636', 800); // ض (Dad)

        // Zha - value 900
        LETTER_VALUES.put('\u0638', 900); // ظ (Zha)

        // Ghain - value 1000
        LETTER_VALUES.put('\u063A', 1000); // غ (Ghain)
    }

    // Days of the week in Urdu
    private static final String[] DAYS_URDU = {
        "اتوار",    // Sunday
        "پیر",      // Monday
        "منگل",     // Tuesday
        "بدھ",      // Wednesday
        "جمعرات",   // Thursday
        "جمعہ",     // Friday
        "ہفتہ"      // Saturday
    };

    private static final String[] DAYS_ENGLISH = {
        "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"
    };

    // Personality type results
    public static final int PERSONALITY_ENEMY = 1;
    public static final int PERSONALITY_FRIEND = 2;
    public static final int PERSONALITY_BUNIYA = 3;
    public static final int PERSONALITY_BUSINESS = 0;

    /**
     * Calculate the Abjad total for a given Urdu/Arabic name.
     * Only counts unique letters (each letter counted once regardless of repetition).
     *
     * @param urduName The name in Urdu/Arabic script
     * @return The total Abjad value
     */
    public static int calculateTotal(String urduName) {
        if (urduName == null || urduName.trim().isEmpty()) {
            return 0;
        }

        String cleanName = urduName.replaceAll("\\s", "");
        Set<Character> uniqueLetters = new HashSet<>();

        for (char c : cleanName.toCharArray()) {
            uniqueLetters.add(c);
        }

        int total = 0;
        for (char letter : uniqueLetters) {
            Integer value = LETTER_VALUES.get(letter);
            if (value != null) {
                total += value;
            }
        }

        return total;
    }

    /**
     * Calculate personality match between two people.
     *
     * @param nameA First person's name in Urdu
     * @param nameB Second person's name in Urdu
     * @return Result object containing totals and personality type
     */
    public static PersonalityResult calculatePersonalityMatch(String nameA, String nameB) {
        int totalA = calculateTotal(nameA);
        int totalB = calculateTotal(nameB);
        int grandTotal = totalA + totalB;
        int remainder = grandTotal % 4;

        String personalityType;
        switch (remainder) {
            case PERSONALITY_ENEMY:
                personalityType = "Enemy";
                break;
            case PERSONALITY_FRIEND:
                personalityType = "Friend";
                break;
            case PERSONALITY_BUNIYA:
                personalityType = "Buniya / Not Trusty";
                break;
            case PERSONALITY_BUSINESS:
            default:
                personalityType = "Business / Both want profit";
                break;
        }

        return new PersonalityResult(totalA, totalB, grandTotal, remainder, personalityType);
    }

    /**
     * Calculate marriage match percentage.
     *
     * @param personA First person's name
     * @param motherA First person's mother's name
     * @param personB Second person's name
     * @param motherB Second person's mother's name
     * @return Result object containing PM, MM, status, and success ratio
     */
    public static MarriageResult calculateMarriageMatch(String personA, String motherA,
                                                         String personB, String motherB) {
        int totalA = calculateTotal(personA);
        int totalAMother = calculateTotal(motherA);
        int totalB = calculateTotal(personB);
        int totalBMother = calculateTotal(motherB);

        // Calculate PM
        int pm = totalA % 4;

        // Calculate MM
        int grandTotal = totalA + totalAMother + totalB + totalBMother;
        int mm = grandTotal % 3;
        int finalMM = (mm == 0) ? 3 : mm;

        // Determine status
        String status;
        switch (finalMM) {
            case 1:
                status = "Good";
                break;
            case 2:
                status = "Not Good";
                break;
            case 3:
            default:
                status = "Average";
                break;
        }

        // Calculate success ratio based on PM-MM combination
        int successRatio = getMarriageSuccessRatio(pm, finalMM);

        return new MarriageResult(pm, finalMM, status, successRatio);
    }

    private static int getMarriageSuccessRatio(int pm, int mm) {
        // Success ratio lookup table
        Map<String, Integer> ratios = new HashMap<>();
        ratios.put("2-1", 80);
        ratios.put("1-1", 7);
        ratios.put("1-2", 5);
        ratios.put("1-3", 6);
        ratios.put("3-1", 55);
        ratios.put("3-2", 21);
        ratios.put("3-3", 42);
        ratios.put("0-1", 73);
        ratios.put("0-2", 50);
        ratios.put("0-3", 67);

        String key = pm + "-" + mm;
        Integer ratio = ratios.get(key);
        return (ratio != null) ? ratio : 0;
    }

    /**
     * Calculate child name compatibility with parents.
     *
     * @param childName Child's name
     * @param fatherName Father's name
     * @param motherName Mother's name
     * @return Result object containing totals, relationships, and final percentage
     */
    public static ChildNameResult calculateChildName(String childName, String fatherName, String motherName) {
        int totalChild = calculateTotal(childName);
        int totalFather = calculateTotal(fatherName);
        int totalMother = calculateTotal(motherName);

        // Child + Father relationship
        int grandTotalFather = totalChild + totalFather;
        int remainderFather = grandTotalFather % 4;

        // Child + Mother relationship
        int grandTotalMother = totalChild + totalMother;
        int remainderMother = grandTotalMother % 4;

        // Get personality types
        String relationshipFather = getPersonalityType(remainderFather);
        String relationshipMother = getPersonalityType(remainderMother);

        // Calculate final percentage
        int finalPercentage = getChildNamePercentage(remainderFather, remainderMother);

        return new ChildNameResult(totalChild, totalFather, totalMother,
                                   relationshipFather, relationshipMother, finalPercentage);
    }

    private static String getPersonalityType(int remainder) {
        switch (remainder) {
            case PERSONALITY_ENEMY:
                return "Enemy";
            case PERSONALITY_FRIEND:
                return "Friend";
            case PERSONALITY_BUNIYA:
                return "Buniya / Not Trusty";
            case PERSONALITY_BUSINESS:
            default:
                return "Business / Both want profit";
        }
    }

    private static int getChildNamePercentage(int remainderFather, int remainderMother) {
        Map<String, Integer> percentages = new HashMap<>();
        percentages.put("2-2", 100);
        percentages.put("0-0", 100);
        percentages.put("1-1", 0);
        percentages.put("3-3", 0);
        percentages.put("1-2", 0);
        percentages.put("2-1", 0);
        percentages.put("3-2", 50);
        percentages.put("2-3", 50);
        percentages.put("2-0", 72);
        percentages.put("0-2", 72);
        percentages.put("3-0", 46);
        percentages.put("0-3", 46);
        percentages.put("1-3", 21);
        percentages.put("3-1", 21);
        percentages.put("1-0", 42);
        percentages.put("0-1", 42);

        String key = remainderFather + "-" + remainderMother;
        Integer percentage = percentages.get(key);
        return (percentage != null) ? percentage : 0;
    }

    /**
     * Calculate Magic (Yes/No) result.
     *
     * @param personName Person's name
     * @param motherName Person's mother's name
     * @return Result object containing calculation details and answer
     */
    public static MagicResult calculateMagic(String personName, String motherName) {
        int totalPerson = calculateTotal(personName);
        int totalMother = calculateTotal(motherName);
        int personsTotal = totalPerson + totalMother;

        // Get today's day
        Calendar calendar = Calendar.getInstance();
        int dayIndex = calendar.get(Calendar.DAY_OF_WEEK) - 1; // 0 = Sunday
        String todayEnglish = DAYS_ENGLISH[dayIndex];
        String todayUrdu = DAYS_URDU[dayIndex];

        // Calculate day total
        int dayTotal = calculateTotal(todayUrdu);

        // Grand total
        int grandTotal = personsTotal + dayTotal;

        // Divide by 4 - if remainder is 0 then "Yes", else "No"
        int remainder = grandTotal % 4;
        String answer = (remainder == 0) ? "Yes" : "No";

        return new MagicResult(totalPerson, totalMother, personsTotal,
                               todayEnglish, todayUrdu, dayTotal, grandTotal, remainder, answer);
    }

    /**
     * Calculate Lost Item result.
     *
     * @param personName Person's name
     * @return Result object containing calculation details and message
     */
    public static LostItemResult calculateLostItem(String personName) {
        int totalPerson = calculateTotal(personName);

        // Get today's day
        Calendar calendar = Calendar.getInstance();
        int dayIndex = calendar.get(Calendar.DAY_OF_WEEK) - 1; // 0 = Sunday
        String todayEnglish = DAYS_ENGLISH[dayIndex];
        String todayUrdu = DAYS_URDU[dayIndex];

        // Calculate day total
        int dayTotal = calculateTotal(todayUrdu);

        // Grand total
        int grandTotal = totalPerson + dayTotal;

        // Divide by 3
        int remainder = grandTotal % 3;

        // Get result message
        String message;
        switch (remainder) {
            case 1:
                message = "In house or near to your reach. Insha Allah you will find it soon.";
                break;
            case 2:
                message = "Misplaced at a different place. You will find it Insha Allah but after some time.";
                break;
            case 0:
            default:
                message = "Not possible or difficult to find it.";
                break;
        }

        return new LostItemResult(totalPerson, todayEnglish, todayUrdu,
                                  dayTotal, grandTotal, remainder, message);
    }

    /**
     * Check if text contains Urdu/Arabic characters.
     */
    public static boolean isUrduText(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }
        // Unicode range for Arabic/Urdu characters
        return text.matches(".*[\\u0600-\\u06FF\\u0750-\\u077F\\u08A0-\\u08FF].*");
    }

    // Result classes

    public static class PersonalityResult {
        public final int totalA;
        public final int totalB;
        public final int grandTotal;
        public final int remainder;
        public final String personalityType;

        public PersonalityResult(int totalA, int totalB, int grandTotal,
                                 int remainder, String personalityType) {
            this.totalA = totalA;
            this.totalB = totalB;
            this.grandTotal = grandTotal;
            this.remainder = remainder;
            this.personalityType = personalityType;
        }
    }

    public static class MarriageResult {
        public final int pm;
        public final int mm;
        public final String status;
        public final int successRatio;

        public MarriageResult(int pm, int mm, String status, int successRatio) {
            this.pm = pm;
            this.mm = mm;
            this.status = status;
            this.successRatio = successRatio;
        }
    }

    public static class ChildNameResult {
        public final int totalChild;
        public final int totalFather;
        public final int totalMother;
        public final String relationshipFather;
        public final String relationshipMother;
        public final int finalPercentage;

        public ChildNameResult(int totalChild, int totalFather, int totalMother,
                               String relationshipFather, String relationshipMother,
                               int finalPercentage) {
            this.totalChild = totalChild;
            this.totalFather = totalFather;
            this.totalMother = totalMother;
            this.relationshipFather = relationshipFather;
            this.relationshipMother = relationshipMother;
            this.finalPercentage = finalPercentage;
        }
    }

    public static class MagicResult {
        public final int totalPerson;
        public final int totalMother;
        public final int personsTotal;
        public final String todayEnglish;
        public final String todayUrdu;
        public final int dayTotal;
        public final int grandTotal;
        public final int remainder;
        public final String answer;

        public MagicResult(int totalPerson, int totalMother, int personsTotal,
                          String todayEnglish, String todayUrdu, int dayTotal,
                          int grandTotal, int remainder, String answer) {
            this.totalPerson = totalPerson;
            this.totalMother = totalMother;
            this.personsTotal = personsTotal;
            this.todayEnglish = todayEnglish;
            this.todayUrdu = todayUrdu;
            this.dayTotal = dayTotal;
            this.grandTotal = grandTotal;
            this.remainder = remainder;
            this.answer = answer;
        }
    }

    public static class LostItemResult {
        public final int totalPerson;
        public final String todayEnglish;
        public final String todayUrdu;
        public final int dayTotal;
        public final int grandTotal;
        public final int remainder;
        public final String message;

        public LostItemResult(int totalPerson, String todayEnglish, String todayUrdu,
                             int dayTotal, int grandTotal, int remainder, String message) {
            this.totalPerson = totalPerson;
            this.todayEnglish = todayEnglish;
            this.todayUrdu = todayUrdu;
            this.dayTotal = dayTotal;
            this.grandTotal = grandTotal;
            this.remainder = remainder;
            this.message = message;
        }
    }
}
