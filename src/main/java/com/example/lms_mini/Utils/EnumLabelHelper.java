package com.example.lms_mini.Utils;

import com.example.lms_mini.enums.CourseLanguage;
import com.example.lms_mini.enums.CourseLevel;
import com.example.lms_mini.enums.Gender;
import com.example.lms_mini.enums.LessonType;
import com.example.lms_mini.enums.Status;

/**
 * Chuyển enum sang tên hiển thị tiếng Việt.
 * Dùng cho file Excel xuất ra (người dùng cuối đọc, không nên thấy MALE/ACTIVE/BEGINNER...).
 */
public final class EnumLabelHelper {

    private EnumLabelHelper() {
    }

    public static String label(Gender gender) {
        if (gender == null) {
            return "";
        }
        switch (gender) {
            case MALE:
                return "Nam";
            case FEMALE:
                return "Nữ";
            case OTHER:
                return "Khác";
            default:
                return gender.name();
        }
    }

    public static String label(CourseLevel level) {
        if (level == null) {
            return "";
        }
        switch (level) {
            case BEGINNER:
                return "Cơ bản";
            case INTERMEDIATE:
                return "Trung cấp";
            case ADVANCED:
                return "Nâng cao";
            default:
                return level.name();
        }
    }

    public static String label(CourseLanguage language) {
        if (language == null) {
            return "";
        }
        switch (language) {
            case VIETNAMESE:
                return "Tiếng Việt";
            case ENGLISH:
                return "Tiếng Anh";
            default:
                return language.name();
        }
    }

    public static String label(LessonType type) {
        if (type == null) {
            return "";
        }
        switch (type) {
            case VIDEO:
                return "Video";
            case DOCUMENT:
                return "Tài liệu";
            case QUIZ:
                return "Quiz";
            default:
                return type.name();
        }
    }

    public static String label(Status status) {
        if (status == null) {
            return "";
        }
        switch (status) {
            case ACTIVE:
                return "Hoạt động";
            case DELETE:
                return "Đã xóa";
            default:
                return status.name();
        }
    }
}
