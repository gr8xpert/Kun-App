package com.example.kunworld.data.models;

import com.example.kunworld.R;

import java.util.Arrays;
import java.util.List;

public class BookData {
    private final String id;
    private final String title;
    private final String author;
    private final String description;
    private final int imageRes;
    private final String pdfFileName;
    private final String category;
    private final int pageCount;

    public BookData(String id, String title, String author, String description,
                    int imageRes, String pdfFileName, String category, int pageCount) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.description = description;
        this.imageRes = imageRes;
        this.pdfFileName = pdfFileName;
        this.category = category;
        this.pageCount = pageCount;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getDescription() { return description; }
    public int getImageRes() { return imageRes; }
    public String getPdfFileName() { return pdfFileName; }
    public String getCategory() { return category; }
    public int getPageCount() { return pageCount; }

    // Static method to get a book by ID
    public static BookData getBookById(String id) {
        for (BookData book : getAllBooks()) {
            if (book.getId().equals(id)) {
                return book;
            }
        }
        return null;
    }

    // Static method to get all books
    public static List<BookData> getAllBooks() {
        return Arrays.asList(
            new BookData(
                "journey_success",
                "A Journey from Comfort to Success Zone",
                "Sagheer Ahmed",
                "Transform your life by stepping out of your comfort zone and achieving success through practical strategies and mindset shifts.",
                R.drawable.book_journey_success,
                "book_journey_success.pdf",
                "Self Development",
                45
            ),
            new BookData(
                "entrepreneurship_women",
                "Entrepreneurship Development Program for Women",
                "Sagheer Ahmed",
                "A comprehensive guide for women entrepreneurs covering business planning, financial management, and leadership skills.",
                R.drawable.book_entrepreneurship_women,
                "book_entrepreneurship_women.pdf",
                "Entrepreneurship",
                38
            ),
            new BookData(
                "great_advices",
                "Great Advices of Great Entrepreneurs",
                "Sagheer Ahmed",
                "Learn from the wisdom of successful entrepreneurs. Practical advice and insights to help you on your entrepreneurial journey.",
                R.drawable.book_great_advices,
                "book_great_advices.pdf",
                "Business",
                42
            ),
            new BookData(
                "personality_character",
                "Human Personality Development Through Character",
                "Sagheer Ahmed",
                "Develop your personality through character building. Understanding the connection between character traits and personal growth.",
                R.drawable.book_personality_character,
                "book_personality_character.pdf",
                "Personality",
                28
            ),
            new BookData(
                "istikhara",
                "Istikhara",
                "Sagheer Ahmed",
                "A complete guide to Istikhara - the Islamic practice of seeking guidance from Allah in making important life decisions.",
                R.drawable.book_istikhara,
                "book_istikhara.pdf",
                "Islamic",
                15
            ),
            new BookData(
                "pregnancy_guide",
                "Pregnancy Guide Book",
                "Sagheer Ahmed",
                "Essential guide for expecting mothers covering health, nutrition, and spiritual guidance during pregnancy.",
                R.drawable.book_pregnancy_guide,
                "book_pregnancy_guide.pdf",
                "Health",
                32
            )
        );
    }
}
