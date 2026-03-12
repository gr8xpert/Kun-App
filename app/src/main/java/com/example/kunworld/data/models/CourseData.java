package com.example.kunworld.data.models;

import com.example.kunworld.R;

import java.util.Arrays;
import java.util.List;

public class CourseData {
    private final String id;
    private final String title;
    private final String tagline;
    private final String description;
    private final int imageRes;
    private final String htmlContent;
    private final List<String> features;
    private final int accentColor;
    private final String duration;
    private final int moduleCount;

    public CourseData(String id, String title, String tagline, String description, int imageRes,
                      String htmlContent, List<String> features, int accentColor,
                      String duration, int moduleCount) {
        this.id = id;
        this.title = title;
        this.tagline = tagline;
        this.description = description;
        this.imageRes = imageRes;
        this.htmlContent = htmlContent;
        this.features = features;
        this.accentColor = accentColor;
        this.duration = duration;
        this.moduleCount = moduleCount;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getTagline() { return tagline; }
    public String getDescription() { return description; }
    public int getImageRes() { return imageRes; }
    public String getHtmlContent() { return htmlContent; }
    public List<String> getFeatures() { return features; }
    public int getAccentColor() { return accentColor; }
    public String getDuration() { return duration; }
    public int getModuleCount() { return moduleCount; }

    // Static method to get a course by ID
    public static CourseData getCourseById(String id) {
        for (CourseData course : getAllCourses()) {
            if (course.getId().equals(id)) {
                return course;
            }
        }
        return null;
    }

    // Static method to get all courses
    public static List<CourseData> getAllCourses() {
        return Arrays.asList(
            new CourseData(
                "personality_development",
                "Personality Development",
                "Develop your true potential",
                "In order to survive like a successful person in today's world, one needs to be smart and quick all the time. Therefore every person needs to develop his/her personality.",
                R.drawable.course_personality_development,
                PERSONALITY_DEVELOPMENT_HTML,
                Arrays.asList(
                    "Faith & Self-Belief",
                    "Physical & Psychological Perspective",
                    "Role of Hormones",
                    "Self-Control",
                    "Emotional Intelligence",
                    "Interpersonal Skills"
                ),
                R.color.primary,
                "8 Weeks",
                9
            ),
            new CourseData(
                "pd_plus",
                "Personality Development Plus",
                "Boost your personality to the next level",
                "This course will boost your personality and will shine your skills with advanced techniques.",
                R.drawable.course_pd_plus,
                PD_PLUS_HTML,
                Arrays.asList(
                    "Physical & Hormonal Knowledge",
                    "Self-Control & Discipline",
                    "Emotional Intelligence",
                    "Interpersonal & Intrapersonal Skills",
                    "Role of Technology",
                    "Role of Religion"
                ),
                R.color.secondary,
                "6 Weeks",
                6
            ),
            new CourseData(
                "communication_skills",
                "Communication Skills",
                "Master the art of effective communication",
                "Effective communication is the foundation on which companies and careers are built and a crucial component of lasting success.",
                R.drawable.course_communication_skills,
                COMMUNICATION_SKILLS_HTML,
                Arrays.asList(
                    "Interpersonal Skills",
                    "Assertiveness Training",
                    "Listening Skills",
                    "Body Language",
                    "Public Speaking",
                    "Job Interview Skills"
                ),
                R.color.tertiary,
                "10 Weeks",
                17
            ),
            new CourseData(
                "entrepreneurship",
                "Entrepreneurship Course",
                "Turn your ideas into successful ventures",
                "First you find your skills, then you shine your skills, and then sell your skills. An individual who undertakes the risk associated with creating, organizing, and owning a business.",
                R.drawable.course_entrepreneurship,
                ENTREPRENEURSHIP_HTML,
                Arrays.asList(
                    "Personality Development",
                    "Creativity & Time Topology",
                    "Business Plan Development",
                    "Company & Branding",
                    "Marketing & Sales",
                    "Finance Management"
                ),
                R.color.accent,
                "12 Weeks",
                12
            ),
            new CourseData(
                "positive_mindset",
                "Positive Thinking & Mindset",
                "Transform your thinking, transform your life",
                "Positive thinking just means that you approach unpleasantness in a more positive and productive way. You think the best is going to happen, not the worst.",
                R.drawable.course_positive_mindset,
                POSITIVE_MINDSET_HTML,
                Arrays.asList(
                    "Psychology of Positivity",
                    "NLP Techniques",
                    "Visualization & Mindfulness",
                    "Change Management",
                    "Problem-Solving",
                    "Mind over Mood"
                ),
                R.color.success,
                "8 Weeks",
                8
            ),
            new CourseData(
                "health_awareness",
                "Human Health Awareness",
                "Understand your body, live healthier",
                "The best way to prevent diseases is by taking precautions and understanding how your body works.",
                R.drawable.course_health_awareness,
                HEALTH_AWARENESS_HTML,
                Arrays.asList(
                    "Vital Organs & Diseases",
                    "Endocrine System",
                    "Psychological Health",
                    "Knowledge of Drugs & Herbs",
                    "Fitness Exercises",
                    "Crisis Management"
                ),
                R.color.error,
                "10 Weeks",
                12
            ),
            new CourseData(
                "visual_philosophy",
                "Visual Philosophy",
                "Develop a process of thinking",
                "Philosophy means the search for truth and visual philosophy is the subject that teaches how to do it through visualization of the process involved in the search.",
                R.drawable.course_visual_philosophy,
                VISUAL_PHILOSOPHY_HTML,
                Arrays.asList(
                    "Human Brain Anatomy",
                    "Language & Communication",
                    "Ideas & Concepts",
                    "Logic & Reasoning",
                    "Knowledge Systems",
                    "Philosophy of Mind"
                ),
                R.color.primary_dark,
                "12 Weeks",
                6
            ),
            new CourseData(
                "habits_changes",
                "Habits and Change (NAC)",
                "Transform your habits, transform your life",
                "If you have habits you want to change or are disturbed psychologically due to failed relationships, unsuccessful marriage, or other problems — join this course and come back to a happy life.",
                R.drawable.course_habits_changes,
                HABITS_CHANGES_HTML,
                Arrays.asList(
                    "Decision Making",
                    "Leverage & Motivation",
                    "Timing & Strategy",
                    "Breaking Patterns",
                    "Replacement Theory",
                    "Consistency Building"
                ),
                R.color.warning,
                "7 Weeks",
                7
            ),
            new CourseData(
                "marriage_awareness",
                "Marriage Awareness",
                "Build a successful and lasting marriage",
                "Marriage is a vital part of a Muslim's life. Learn how to manage conflicts, keep the spark alive, and build a lasting relationship according to Islamic principles.",
                R.drawable.course_marriage_awareness,
                MARRIAGE_AWARENESS_HTML,
                Arrays.asList(
                    "Sharia Guidelines",
                    "Transition & Health",
                    "Social Habits",
                    "Personality Understanding",
                    "In-laws Relations",
                    "Parenting Skills"
                ),
                R.color.tertiary,
                "10 Weeks",
                10
            )
        );
    }

    // HTML Content for each course
    private static final String PERSONALITY_DEVELOPMENT_HTML =
        "<h1>Personality Development</h1>" +
        "<p>In order to survive like a successful person in today's world, one needs to be smart and quick all the time. Therefore every person needs to develop his/her personality. Everyone would like for the people around them to respect and admire them. It's no longer just about how much effort you put into your work — one's personality also has a lot to do with what one achieves.</p>" +
        "<p>Personality traits are defined as the relatively enduring patterns of thoughts, feelings, and behaviors that distinguish individuals from one another.</p>" +
        "<p>We compiled your hope in the form of the course \"Personality Development\".</p>" +
        "<h2>Course Outline</h2>" +
        "<ol>" +
        "<li>Faith</li>" +
        "<li>Human According to Physical and Psychological Perspective</li>" +
        "<li>Role of Hormones in Our Decisions</li>" +
        "<li>Self-Control</li>" +
        "<li>Skills and Their Types</li>" +
        "<li>Emotional Intelligence</li>" +
        "<li>Role of Technology</li>" +
        "<li>Interpersonal Skills</li>" +
        "<li>Intrapersonal Skills</li>" +
        "</ol>";

    private static final String PD_PLUS_HTML =
        "<h1>Personality Development Plus</h1>" +
        "<p>I feel honoured to announce the new online \"Personality Development Plus\" course, after the appreciation of all previous courses. This course will boost your personality and will shine your skills.</p>" +
        "<h2>Course Outline</h2>" +
        "<ul>" +
        "<li>Physical, Psychological, and Hormonal Knowledge</li>" +
        "<li>Self-control, Concentration, and Self-discipline</li>" +
        "<li>Emotional Intelligence and Dos/Don'ts</li>" +
        "<li>Interpersonal and Intrapersonal Skills</li>" +
        "<li>Role of Technology</li>" +
        "<li>Role of Religion</li>" +
        "</ul>" +
        "<p>And many many more according to your character.</p>";

    private static final String COMMUNICATION_SKILLS_HTML =
        "<h1>Communication Skills</h1>" +
        "<p>Effective communication is the foundation on which companies and careers are built and a crucial component of lasting success.</p>" +
        "<p>Whether the audience is an entire organization or a single individual, effective communication requires bringing together different points of view and relaying that information without losing clarity or focus...</p>" +
        "<h2>Course Outline</h2>" +
        "<ol>" +
        "<li>Introduction and Course Overview</li>" +
        "<li>Interpersonal Skills</li>" +
        "<li>Overview of Communication</li>" +
        "<li>Communication Barriers</li>" +
        "<li>Communication Skills</li>" +
        "<li>How to Get People On Your Side?</li>" +
        "<li>The Wheel of Communion</li>" +
        "<li>The Importance of Voice</li>" +
        "<li>Assertiveness and How to Say No</li>" +
        "<li>Developing Assertiveness</li>" +
        "<li>Listening Skills</li>" +
        "<li>Indirect Communication</li>" +
        "<li>Body Language and Communication</li>" +
        "<li>Giving and Receiving Feedback</li>" +
        "<li>Your Personal Action Plan</li>" +
        "<li>Public Speaking</li>" +
        "<li>Job Interview</li>" +
        "</ol>";

    private static final String ENTREPRENEURSHIP_HTML =
        "<h1>Entrepreneurship Course</h1>" +
        "<p>There are many definitions of the concept of \"entrepreneurship\". But my knowledge is coinciding with brain-body-soul at the following:</p>" +
        "<blockquote>" +
        "<p>First you find your skills, then you shine your skills, and then sell your skills...</p>" +
        "<p>Or</p>" +
        "<p>An individual who undertakes the risk associated with creating, organizing, and owning a business...</p>" +
        "</blockquote>" +
        "<p>So, according to the above definition it is the need of every person i.e. male/female, girls/boys, young/old, student/businessman, etc.</p>" +
        "<p>Entrepreneurship is a process through which individuals identify opportunities, allocate resources, and create value. The behavior of the entrepreneur reflects a kind of person willing to put his or her career and financial security on the line and take risks in the name of an idea, spending much time as well as capital on an uncertain venture. A person that has the potential for bringing positive change is called an entrepreneur.</p>" +
        "<h2>Course Outline</h2>" +
        "<ol>" +
        "<li>Personality and Personality Development</li>" +
        "<li>Hormones and Their Role in Our Personality</li>" +
        "<li>Entrepreneurship</li>" +
        "<li>Creativity</li>" +
        "<li>Time Topology</li>" +
        "<li>Business Plan</li>" +
        "<li>Company / Branding</li>" +
        "<li>Role of Technology</li>" +
        "<li>Game Theory / Operation Research</li>" +
        "<li>Promotion / Marketing / Selling</li>" +
        "<li>Finance</li>" +
        "<li>Small Business Ideas</li>" +
        "</ol>";

    private static final String POSITIVE_MINDSET_HTML =
        "<h1>Positive Thinking and Mindset</h1>" +
        "<p>Positive thinking just means that you approach unpleasantness in a more positive and productive way. You think the best is going to happen, not the worst. Positive thinking often starts with self-talk. Self-talk is the endless stream of unspoken thoughts that run through your head.</p>" +
        "<h2>Course Outline</h2>" +
        "<p>Below are the important modules that will be facilitated in the Positive Thinking and Mindset Training Course:</p>" +
        "<h2>Module 1</h2>" +
        "<ul>" +
        "<li>Psychology of Positivity: Introduction</li>" +
        "<li>Optimism versus Pessimism</li>" +
        "<li>Science and Power of the Human Brain</li>" +
        "<li>Events Affecting Positive and Negative Thinking</li>" +
        "<li>Tactics for Transforming Negative Words and Experiences</li>" +
        "<li>Techniques to Cultivate Positive Thinking Patterns</li>" +
        "<li>Develop Positive Communication: Art of Positive Speaking</li>" +
        "<li>Going Beyond Set Limits with Your Thinking</li>" +
        "</ul>" +
        "<h2>Module 2</h2>" +
        "<ul>" +
        "<li>Thinking in Different Approaches and Backgrounds</li>" +
        "<li>Examining and Evaluating Existing Mindsets of Others</li>" +
        "<li>Strategies for Eliminating Obstacles to Positive Thinking</li>" +
        "<li>Left and Right Brain Thinking: Vertical and Lateral Hemispheres</li>" +
        "<li>Characterizing Your Own Style of Thinking and Mindset</li>" +
        "<li>Developing the Originality and Innovation Mindset</li>" +
        "<li>Lateral and Creativity Thinking</li>" +
        "<li>Six Thinking Hats: Edward de Bono</li>" +
        "<li>The Mind and Building Positive Relationships</li>" +
        "<li>The Mind and Causing Toxic Relationships</li>" +
        "</ul>" +
        "<h2>Module 3</h2>" +
        "<ul>" +
        "<li>Understanding People and Negative Behaviour</li>" +
        "<li>Introducing Others to a World of Positivity</li>" +
        "<li>Workplace Negativity: How It Begins</li>" +
        "<li>Workplace Negativity: How to End</li>" +
        "<li>Demotivation: The Negativity Booster</li>" +
        "<li>The Virus of Demotivation</li>" +
        "<li>Radiating and Sustaining a Positivity Force Field around You</li>" +
        "<li>Techniques to Present Change, Ideas, and Criticism Positively</li>" +
        "<li>Self / Personal Development</li>" +
        "<li>Step-by-Step Action Plan for Developing Positivity</li>" +
        "</ul>" +
        "<h2>Module 4</h2>" +
        "<ul>" +
        "<li>Autogenic Conditioning of the Mind</li>" +
        "<li>Programming the Unconscious Mind</li>" +
        "<li>Train Your Mind to Train Your Body</li>" +
        "<li>Mindfulness and Visualization</li>" +
        "<li>Anxiety Control and Meditation</li>" +
        "<li>Visualization Techniques and Relaxation Techniques</li>" +
        "<li>Visualization and Mental Imagery</li>" +
        "<li>Visualization: The PETTLEP Model</li>" +
        "<li>Practical Positive Thinking Tools and Methods</li>" +
        "<li>Becoming More Proactive and Seizing Control</li>" +
        "</ul>" +
        "<h2>Module 5</h2>" +
        "<ul>" +
        "<li>Neuro-Linguistic Programming (NLP): Introduction</li>" +
        "<li>Historical Practice and Applications of NLP</li>" +
        "<li>Positive Thinking Based on NLP Techniques</li>" +
        "<li>Meta and Milton Conversation Model</li>" +
        "<li>Swish and Reframing Models</li>" +
        "<li>Power and Principles of Empowering Beliefs</li>" +
        "<li>Meeting Deadlines & Dealing with Stress</li>" +
        "<li>Thinking Positively to Reduce Time Wastage</li>" +
        "<li>Defeating Stress by a Positive Mental Attitude</li>" +
        "<li>Reacting and Responding to Reactions</li>" +
        "</ul>" +
        "<h2>Module 6</h2>" +
        "<ul>" +
        "<li>Process of Change: The Inevitable</li>" +
        "<li>Getting Ready to Change Your Thinking Patterns</li>" +
        "<li>Identifying Negativity in Words and Thoughts</li>" +
        "<li>Change Management: From Negative to Positive</li>" +
        "<li>Making Positive Thinking Create Opportunities</li>" +
        "<li>Deleting the History of Old Negative Beliefs and Foundations</li>" +
        "<li>Reprogramming Your Thinking and Mindset</li>" +
        "</ul>" +
        "<h2>Module 7</h2>" +
        "<ul>" +
        "<li>Problem-Solving through Positive Thinking</li>" +
        "<li>Identifying the Root of the Problem</li>" +
        "<li>Getting the Mindset Right and Focused on Solutions</li>" +
        "<li>Creating Practical, Inventive, and Positive Solutions</li>" +
        "<li>Setting Priorities: Focusing on What's Important</li>" +
        "<li>Setting Work and Life Objectives for Success</li>" +
        "<li>SWOT Analysis: Positive Side of Life</li>" +
        "</ul>" +
        "<h2>Module 8</h2>" +
        "<ul>" +
        "<li>Energy and Vibes Transfer Between People</li>" +
        "<li>Mind over Mood / Emotions</li>" +
        "<li>Relation Between Negative Thinking and Ill-health</li>" +
        "<li>Influence of the Mind to Interrupt Change</li>" +
        "<li>Breathing and Relaxing Influences on Positive Thinking</li>" +
        "<li>Preparing for Future Negative Scenarios</li>" +
        "<li>Living a Healthy Lifestyle</li>" +
        "<li>Excellent Tips for Positive Thinking</li>" +
        "<li>Top Tips for Mindset Change</li>" +
        "</ul>";

    private static final String HEALTH_AWARENESS_HTML =
        "<h1>Human Health Awareness</h1>" +
        "<p>The best way to prevent dengue fever is by taking precautions to avoid being bitten by mosquitoes, which include:</p>" +
        "<ul>" +
        "<li>Using a mosquito repellent containing DEET, or oil of lemon eucalyptus.</li>" +
        "<li>Dressing in protective clothing during the day — long-sleeved shirts, long pants, socks, and shoes — especially in early morning hours before daybreak and in late afternoon after dark.</li>" +
        "<li>Keeping unscreened windows and doors closed.</li>" +
        "<li>Getting rid of areas where mosquitoes breed, such as standing water in flower pots, containers, and bamboos etc.</li>" +
        "</ul>" +
        "<p>After treatment and recovery, for fitness one must do proper exercise (i.e. the role of exercise).</p>" +
        "<p>The above example shows the importance of the course \"Human Health Awareness\".</p>" +
        "<h2>Course Outline</h2>" +
        "<ol>" +
        "<li>Difference Between a Healthy and Ill Body</li>" +
        "<li>Vital Organs, Their Role, and Their Diseases</li>" +
        "<li>Endocrine System, Hormones, and Their Diseases</li>" +
        "<li>Bones and Their Diseases</li>" +
        "<li>Brain and Psychological Diseases</li>" +
        "<li>Knowledge about Drugs</li>" +
        "<li>Knowledge about Herbs</li>" +
        "<li>Muscles and Body Shapes</li>" +
        "<li>Knowledge about Massage</li>" +
        "<li>Knowledge about Fitness Exercises</li>" +
        "<li>Reproductive System and Family Planning</li>" +
        "<li>Crisis Management</li>" +
        "</ol>";

    private static final String VISUAL_PHILOSOPHY_HTML =
        "<h1>Visual Philosophy</h1>" +
        "<p>We are glad to inform you of our new subject, Visual Philosophy. It is a course designed to inculcate and develop a process of thinking in the students. Philosophy means the search for truth and visual philosophy is the subject that teaches how to do it. Rather than only focusing on end results, it focuses on visualization of the process involved in the search. It combines the study of human brain anatomy, human psychology, philosophy, language, logic and mathematics to provide all the necessary tools to the students to better utilize their skills and knowledge in their respective fields.</p>" +
        "<h2>Course Outline</h2>" +
        "<h2>Chapter 1: Introduction</h2>" +
        "<p>Provides an introduction of the subject in general and the course, the reasons for teaching it, its objectives and targets, and the way it should be studied.</p>" +
        "<h2>Chapter 2: Anatomy of the Human Brain</h2>" +
        "<p>Outlines the anatomy of the human brain, describes its sensory systems, memory system and its food requirements. The chapter then goes on to explain the method of working of the human brain, its diseases and finally the Brain-Body relation.</p>" +
        "<h2>Chapter 3: Language</h2>" +
        "<p>Discusses language. Starting with the history of human language, the chapter introduces the pillars of language and the language of the brain. The chapter also discusses the processes of reading and writing and concludes by explaining the different forms of writing.</p>" +
        "<h2>Chapter 4: Ideas</h2>" +
        "<p>Discusses the term ideas in general. The chapter starts with an introduction to the fundamental concepts of the terminology and its importance, explains its types and then concludes with a discussion on the connection between ideas and the physical world.</p>" +
        "<h2>Chapter 5: Logic</h2>" +
        "<p>Focuses on logic. It introduces the basic concepts and discusses the differences between western and eastern concepts of logic. It introduces the two fundamental processes involved in logic, induction and deduction.</p>" +
        "<h2>Chapter 6: Knowledge</h2>" +
        "<p>Concerned with knowledge. The chapter begins with the definition of information. It then discusses the difference between knowledge and information and presents a conversion formula to convert information to knowledge. The chapter concludes with a discussion on the limitations and the extent of human knowledge.</p>" +
        "<p>And many more...</p>";

    private static final String HABITS_CHANGES_HTML =
        "<h1>Habits and Change (NAC Course)</h1>" +
        "<p>If you have habits like smoking, glass huqa, ignoring children or family, eating too much junk food, excessive movies, involvement in inappropriate relationships, and other bad habits that you want to change — please attend our NAC course.</p>" +
        "<p>Also, if you are disturbed psychologically due to a failed relationship, unsuccessful marriage, physical abuse, bad moments of hormonal changes, loss of an important match, or failure in professional exams — if you are disturbed by any type of these problems then you must join this course and come back to a happy life.</p>" +
        "<h2>Course Outline</h2>" +
        "<ol>" +
        "<li><strong>First Session:</strong> The first step to creating any change is deciding what you want so that you have something to move toward.</li>" +
        "<li><strong>Second Session:</strong> Get leverage — associate massive pain to not changing now and massive pleasure to the experience of changing now.</li>" +
        "<li><strong>Third Session:</strong> Timing is everything.</li>" +
        "<li><strong>Fourth Session:</strong> How and why to break and interrupt the limiting pattern.</li>" +
        "<li><strong>Fifth Session:</strong> Importance of the \"Replacement Theory\" and creating a new, empowering alternative.</li>" +
        "<li><strong>Sixth Session:</strong> How to adopt the habits of consistency and condition the new pattern until it is consistent.</li>" +
        "<li><strong>Seventh Session:</strong> By practice, take control of all steps.</li>" +
        "</ol>";

    private static final String MARRIAGE_AWARENESS_HTML =
        "<h1>Marriage Awareness</h1>" +
        "<p>Marriage, also called matrimony or wedlock, is a socially or ritually recognized union or legal contract between spouses that establishes rights and obligations between them. But according to Islam it is beyond that — marriage is a vital part of a Muslim's life. In fact, marriage is so important in the religion of Islam that it is declared to be one half of one's faith.</p>" +
        "<p>As a Muslim one should live in accordance with the Islamic Jurisprudence in the way shown by the greatest of creations and the person who had the greatest impact on mankind in the existence of the universe.</p>" +
        "<p>Marriage is the process by which two people make their relationship public, official, and permanent. It is the joining of two people in a bond that lasts until death, but in practice is increasingly cut short by divorce.</p>" +
        "<p>Over the course of a relationship that can last as many as seven or eight decades, a lot happens. Personalities change, and true married life waxes and wanes. No marriage is free of conflict. What enables a couple to endure is how they handle that conflict. So how do you manage the problems that inevitably arise? And how can you keep the spark alive? For the answers to all these questions we have compiled a short course.</p>" +
        "<h2>Course Outline</h2>" +
        "<ol>" +
        "<li>What Is Marriage According to Sharia? And the Importance of the Marriage Contract</li>" +
        "<li>The Transition Function: Before and After Marriage</li>" +
        "<li>Health Issues and How to Control Them</li>" +
        "<li>Required Social Habits</li>" +
        "<li>Sharia Masa'il</li>" +
        "<li>Understanding Your Own Personality, Skills, and Weaknesses</li>" +
        "<li>Knowing What to Do and What Not to Do</li>" +
        "<li>Common Mistakes Made by Boys and Girls</li>" +
        "<li>How to Develop a Good Relationship with the In-laws</li>" +
        "<li>Being a Good Mother and a Good Father</li>" +
        "</ol>";
}
