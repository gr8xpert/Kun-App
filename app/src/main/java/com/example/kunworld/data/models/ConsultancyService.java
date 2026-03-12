package com.example.kunworld.data.models;

import com.example.kunworld.R;

import java.util.Arrays;
import java.util.List;

public class ConsultancyService {
    private final String id;
    private final String title;
    private final String tagline;
    private final int imageRes;
    private final String htmlContent;
    private final List<String> features;
    private final int accentColor;

    public ConsultancyService(String id, String title, String tagline, int imageRes,
                              String htmlContent, List<String> features, int accentColor) {
        this.id = id;
        this.title = title;
        this.tagline = tagline;
        this.imageRes = imageRes;
        this.htmlContent = htmlContent;
        this.features = features;
        this.accentColor = accentColor;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getTagline() {
        return tagline;
    }

    public int getImageRes() {
        return imageRes;
    }

    public String getHtmlContent() {
        return htmlContent;
    }

    public List<String> getFeatures() {
        return features;
    }

    public int getAccentColor() {
        return accentColor;
    }

    // Static method to get a service by ID
    public static ConsultancyService getServiceById(String id) {
        for (ConsultancyService service : getAllServices()) {
            if (service.getId().equals(id)) {
                return service;
            }
        }
        return null;
    }

    // Static method to get all consultancy services
    public static List<ConsultancyService> getAllServices() {
        return Arrays.asList(
            new ConsultancyService(
                "business",
                "Business Consultancy",
                "Transform your business vision into reality",
                R.drawable.consultancy_business,
                BUSINESS_HTML,
                Arrays.asList(
                    "Business Mindset Training",
                    "Time Topology Techniques",
                    "Business Plan Development",
                    "Finance Management",
                    "Entrepreneurship Course",
                    "Digital Marketing",
                    "Company Registration Guide"
                ),
                R.color.secondary
            ),
            new ConsultancyService(
                "education",
                "Education Consultancy",
                "Unlock your academic potential",
                R.drawable.consultancy_education,
                EDUCATION_HTML,
                Arrays.asList(
                    "Career Counselling",
                    "Syllabus Management",
                    "Time Management",
                    "Art of Learning",
                    "Note Design Techniques",
                    "Book Selection Guide",
                    "Subconscious Mind Training"
                ),
                R.color.primary
            ),
            new ConsultancyService(
                "family",
                "Family Counseling",
                "Build stronger family bonds",
                R.drawable.consultancy_family,
                FAMILY_HTML,
                Arrays.asList(
                    "Parents as Mentors",
                    "Child Development",
                    "Family Discipline",
                    "Diet & Nutrition Plans",
                    "Communication Skills",
                    "Dispute Resolution",
                    "Weekly Sessions"
                ),
                R.color.tertiary
            ),
            new ConsultancyService(
                "health",
                "Health Consultancy",
                "Achieve holistic wellness without medicine",
                R.drawable.consultancy_health,
                HEALTH_HTML,
                Arrays.asList(
                    "Psychological Assessment",
                    "Personalized Fitness",
                    "Pressure Point Training",
                    "NLP Techniques",
                    "Diet Planning",
                    "Massage Therapy",
                    "First Aid Training"
                ),
                R.color.success
            ),
            new ConsultancyService(
                "personality",
                "Personality Coaching",
                "Discover and develop your true self",
                R.drawable.consultancy_personality,
                PERSONALITY_HTML,
                Arrays.asList(
                    "Personality Type Analysis",
                    "Life Purpose Discovery",
                    "Skills Assessment",
                    "Life Planner Design",
                    "Personal Development",
                    "Weekly Lectures",
                    "Short Courses"
                ),
                R.color.accent
            )
        );
    }

    // HTML Content for each consultancy type
    private static final String BUSINESS_HTML =
        "<h2>Introduction</h2>" +
        "<ul>" +
        "<li>Definition of Business Consultancy</li>" +
        "<li>Importance of Business Consultancy</li>" +
        "<li>The Role of Business Consultants</li>" +
        "</ul>" +
        "<h2>The Process of Business Consultancy</h2>" +
        "<ul>" +
        "<li>Initial Consultation</li>" +
        "<li>Assessing the Client's Business History and Current Status</li>" +
        "<li>Collecting Information about the Client's Goals and Objectives</li>" +
        "<li>Identifying Business Issues and Concerns</li>" +
        "<li>Designing a Personalized Business Plan</li>" +
        "</ul>" +
        "<h2>Components of a Business Plan</h2>" +
        "<ul>" +
        "<li>Market Research and Analysis</li>" +
        "<li>Financial Analysis and Forecasting</li>" +
        "<li>Sales and Marketing Strategies</li>" +
        "<li>Operations and Management Strategies</li>" +
        "<li>HR and Talent Management Strategies</li>" +
        "<li>Risk Assessment and Mitigation Strategies</li>" +
        "</ul>" +
        "<h2>Implementation and Follow-up</h2>" +
        "<ul>" +
        "<li>Assisting the Client with Implementing the Business Plan</li>" +
        "<li>Providing Education and Support to the Client</li>" +
        "<li>Follow-up Consultations to Monitor Progress</li>" +
        "<li>Modifying the Business Plan as Necessary</li>" +
        "</ul>" +
        "<h2>Special Considerations</h2>" +
        "<ul>" +
        "<li>Working with Small Businesses and Startups</li>" +
        "<li>Working with Businesses Experiencing Financial or Legal Issues</li>" +
        "<li>Working with Businesses Experiencing Cultural or Diversity Issues</li>" +
        "<li>Working with Businesses Experiencing Technological Challenges</li>" +
        "</ul>" +
        "<h2>Key Features of Business Consultancy</h2>" +
        "<p>There will be very few jobs in the future so everyone has to come into the business. That is why we provide consultancy to all types of businesses, big and small.</p>" +
        "<ol>" +
        "<li>Teach and give training of Business Mind-set</li>" +
        "<li>Teach and give training of \"Time Topology\"</li>" +
        "<li>Teach and give training of \"Business Plan by Sagheer Ahmed\"</li>" +
        "<li>Teach and give training of Finance Management and Finance Optimization</li>" +
        "<li>Teach short course of \"Entrepreneurship\"</li>" +
        "<li>Teach short course of \"PMP\"</li>" +
        "<li>Teach short course of Company Making with Registration</li>" +
        "<li>Small Business Ideas</li>" +
        "<li>Short course of Business Laws with Taxation Rules</li>" +
        "<li>Short course of Digital Marketing</li>" +
        "<li>Teach and train 100 Golden Tips of Business</li>" +
        "</ol>";

    private static final String EDUCATION_HTML =
        "<h2>Introduction</h2>" +
        "<ul>" +
        "<li>Definition of Education Consultancy</li>" +
        "<li>Importance of Education Consultancy</li>" +
        "<li>The Role of Education Consultants</li>" +
        "</ul>" +
        "<h2>The Process of Education Consultancy</h2>" +
        "<ul>" +
        "<li>Initial Consultation</li>" +
        "<li>Gathering Information about the Student's Academic and Personal Background</li>" +
        "<li>Identifying Academic Goals and Objectives</li>" +
        "<li>Assessing the Student's Learning Style, Strengths, and Weaknesses</li>" +
        "<li>Designing a Personalized Education Plan</li>" +
        "</ul>" +
        "<h2>Components of an Education Plan</h2>" +
        "<ul>" +
        "<li>Academic Planning and Course Selection</li>" +
        "<li>College and University Selection and Application Assistance</li>" +
        "<li>Standardized Test Preparation and Study Skills Development</li>" +
        "<li>Scholarship and Financial Aid Assistance</li>" +
        "<li>Extracurricular Activity Planning and Guidance</li>" +
        "</ul>" +
        "<h2>Implementation and Follow-up</h2>" +
        "<ul>" +
        "<li>Assisting the Student with Implementing the Education Plan</li>" +
        "<li>Providing Education and Support to the Student and Their Family</li>" +
        "<li>Follow-up Consultations to Monitor Progress</li>" +
        "<li>Modifying the Education Plan as Necessary</li>" +
        "</ul>" +
        "<h2>Special Considerations</h2>" +
        "<ul>" +
        "<li>Working with Students with Special Needs or Learning Disabilities</li>" +
        "<li>Working with International Students</li>" +
        "<li>Working with Students Interested in Vocational or Technical Education</li>" +
        "<li>Working with Students Interested in Gap Years or Alternative Education Paths</li>" +
        "</ul>" +
        "<h2>We Will Help You In...</h2>" +
        "<ul>" +
        "<li>Career Counselling</li>" +
        "<li>Syllabus Management</li>" +
        "<li>Time Management</li>" +
        "<li>Other than School Syllabus</li>" +
        "<li>How to Design the Notes?</li>" +
        "<li>Art of Listening / Reading / Writing</li>" +
        "<li>Importance of Short Courses in Our Life</li>" +
        "<li>How to Choose the Best Book?</li>" +
        "<li>How to Write the Book?</li>" +
        "<li>Re-wire His / Her Sub-conscious Mind</li>" +
        "</ul>";

    private static final String FAMILY_HTML =
        "<h2>Introduction</h2>" +
        "<ul>" +
        "<li>Definition of Family Consultancy</li>" +
        "<li>Importance of Family Consultancy</li>" +
        "<li>The Role of Family Consultants</li>" +
        "</ul>" +
        "<h2>The Process of Family Consultancy</h2>" +
        "<ul>" +
        "<li>Initial Consultation</li>" +
        "<li>Gathering Information about the Family's History and Current Situation</li>" +
        "<li>Identifying Family Issues and Concerns</li>" +
        "<li>Establishing Goals and Objectives for the Consultation</li>" +
        "<li>Developing a Personalized Plan for the Family</li>" +
        "</ul>" +
        "<h2>Components of a Family Consultation Plan</h2>" +
        "<ul>" +
        "<li>Communication Strategies and Techniques</li>" +
        "<li>Conflict Resolution Techniques</li>" +
        "<li>Parenting Techniques and Strategies</li>" +
        "<li>Family Dynamics and Relationship Building</li>" +
        "<li>Developing Family Rituals and Traditions</li>" +
        "</ul>" +
        "<h2>Implementation and Follow-up</h2>" +
        "<ul>" +
        "<li>Assisting the Family with Implementing the Consultation Plan</li>" +
        "<li>Providing Education and Support to Family Members</li>" +
        "<li>Follow-up Consultations to Monitor Progress</li>" +
        "<li>Modifying the Consultation Plan as Necessary</li>" +
        "</ul>" +
        "<h2>Special Considerations</h2>" +
        "<ul>" +
        "<li>Working with Families Experiencing Divorce or Separation</li>" +
        "<li>Working with Families Experiencing Substance Abuse or Addiction</li>" +
        "<li>Working with Families Experiencing Mental Health Concerns</li>" +
        "<li>Working with Families Experiencing Financial or Legal Issues</li>" +
        "</ul>" +
        "<h2>Key Points for Family Counseling</h2>" +
        "<p>The biggest problem of our society is the untrained parents who not only cause the destruction of their child but also the destruction of the whole society, so it is very important to learn the following points:</p>" +
        "<ol>" +
        "<li>Parents as a \"Mentor\"</li>" +
        "<li>True Needs of Child</li>" +
        "<li>Design Family Discipline Book</li>" +
        "<li>Right Methods of Teaching</li>" +
        "<li>Diet and Nutrients Plan for Individual Family Member</li>" +
        "<li>Awareness about \"Role of Father & Mother\"</li>" +
        "<li>Role of Different Games and Sports</li>" +
        "<li>Role of Stories and Assertive Communication</li>" +
        "<li>Weekly One Hour Session</li>" +
        "<li>Daily Health Tips</li>" +
        "<li>Individual Personality Grooming</li>" +
        "<li>Short Course of Dispute Resolution</li>" +
        "</ol>";

    private static final String HEALTH_HTML =
        "<h2>Introduction</h2>" +
        "<ul>" +
        "<li>Definition of Health Consultancy</li>" +
        "<li>Importance of Health Consultancy</li>" +
        "<li>The Role of Health Consultants</li>" +
        "</ul>" +
        "<h2>The Process of Health Consultancy</h2>" +
        "<ul>" +
        "<li>Initial Consultation</li>" +
        "<li>Assessing the Client's Health History</li>" +
        "<li>Collecting Information about the Client's Current Health Status</li>" +
        "<li>Identifying the Client's Health Goals and Objectives</li>" +
        "<li>Designing a Personalized Health Plan</li>" +
        "</ul>" +
        "<h2>Components of a Health Plan</h2>" +
        "<ul>" +
        "<li>Dietary Recommendations</li>" +
        "<li>Exercise Recommendations</li>" +
        "<li>Stress Management Techniques</li>" +
        "<li>Sleep Hygiene Practices</li>" +
        "<li>Recommendations for Supplements, Vitamins, and Herbs</li>" +
        "<li>Monitoring and Tracking Progress</li>" +
        "</ul>" +
        "<h2>Implementation and Follow-up</h2>" +
        "<ul>" +
        "<li>Assisting the Client with Implementing the Health Plan</li>" +
        "<li>Providing Education and Support to the Client</li>" +
        "<li>Follow-up Consultations to Monitor Progress</li>" +
        "<li>Modifying the Health Plan as Necessary</li>" +
        "</ul>" +
        "<h2>Special Considerations</h2>" +
        "<ul>" +
        "<li>Working with Clients with Chronic Health Conditions</li>" +
        "<li>Working with Clients with Special Dietary Requirements</li>" +
        "<li>Working with Clients with Mental Health Concerns</li>" +
        "<li>Working with Clients with a History of Substance Abuse</li>" +
        "</ul>" +
        "<h2>Health Management Plan (Without Medicine)</h2>" +
        "<p>Only a healthy body has quality thinking. Today, 90% of diseases are due to our thinking, so if you don't understand the following points, you too will become a person with a sick body and brain:</p>" +
        "<ol>" +
        "<li>Psychological and Physical Knowledge</li>" +
        "<li>Diagnosis Pattern and Steps</li>" +
        "<li>Fitness Exercise for Individuals</li>" +
        "<li>Training for Pressure Points of Whole Body</li>" +
        "<li>Short Course of NLP</li>" +
        "<li>Body Alignment with Chiropractor</li>" +
        "<li>Diet Plan According to Your Body</li>" +
        "<li>Training of Massage Therapy</li>" +
        "<li>Solution with Ailm-e-Noorani</li>" +
        "<li>One Hour Session Weekly</li>" +
        "<li>Awareness and Training of \"First Aid\"</li>" +
        "<li>Live Therapeutic Session</li>" +
        "</ol>";

    private static final String PERSONALITY_HTML =
        "<h2>Introduction</h2>" +
        "<ul>" +
        "<li>Definition of Personality Coaching</li>" +
        "<li>Importance of Personality Coaching</li>" +
        "<li>The Role of Personality Coaches</li>" +
        "</ul>" +
        "<h2>The Process of Personality Coaching</h2>" +
        "<ul>" +
        "<li>Initial Consultation</li>" +
        "<li>Assessing the Client's Personality Traits, Strengths, and Weaknesses</li>" +
        "<li>Collecting Information about the Client's Personal and Professional Goals</li>" +
        "<li>Identifying Personality Issues and Concerns</li>" +
        "<li>Designing a Personalized Personality Development Plan</li>" +
        "</ul>" +
        "<h2>Components of a Personality Development Plan</h2>" +
        "<ul>" +
        "<li>Self-awareness and Emotional Intelligence Development</li>" +
        "<li>Communication and Interpersonal Skills Development</li>" +
        "<li>Leadership and Management Skills Development</li>" +
        "<li>Conflict Resolution and Problem-solving Skills Development</li>" +
        "<li>Stress Management and Self-care Practices Development</li>" +
        "</ul>" +
        "<h2>Implementation and Follow-up</h2>" +
        "<ul>" +
        "<li>Assisting the Client with Implementing the Personality Development Plan</li>" +
        "<li>Providing Education and Support to the Client</li>" +
        "<li>Follow-up Consultations to Monitor Progress</li>" +
        "<li>Modifying the Personality Development Plan as Necessary</li>" +
        "</ul>" +
        "<h2>Special Considerations</h2>" +
        "<ul>" +
        "<li>Working with Clients with Specific Personality Disorders</li>" +
        "<li>Working with Clients with Specific Phobias or Fears</li>" +
        "<li>Working with Clients with Specific Professional Development Needs</li>" +
        "<li>Working with Clients with Specific Personal Development Needs</li>" +
        "</ul>" +
        "<h2>We Will Help You In...</h2>" +
        "<ul>" +
        "<li>Find Your Personality Type</li>" +
        "<li>Find Your Purpose of Life</li>" +
        "<li>Applying Sagheer's Skills Analysis Test to Find Your Skills</li>" +
        "<li>Design Your Aim of Life</li>" +
        "<li>Design Your Life Planner Book</li>" +
        "<li>Design Your Diet Chart, Exercise Chart, Reading Materials, and Many More</li>" +
        "<li>One Hour Weekly Lecture</li>" +
        "<li>Many Short Courses Related to Personality Development</li>" +
        "</ul>";
}
