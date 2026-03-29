package com.simats.ruralcareai.model

enum class UserRole(
    val displayName: String,
    val description: String,
    val starterActions: List<String>,
    val backendRole: String,
    val supportsSelfSignup: Boolean,
) {
    ADMIN(
        displayName = "Admin",
        description = "I am an admin managing the healthcare center.",
        starterActions = listOf(
            "View center statistics",
            "Manage doctors and staff",
            "Monitor patient appointments",
            "View center operations"
        ),
        backendRole = "admin",
        supportsSelfSignup = false,
    ),
    PATIENT(
        displayName = "Patient",
        description = "I want to consult with a doctor and manage my health records.",
        starterActions = listOf(
            "Book consultation appointment",
            "View prescribed medications",
            "Check follow-up reminders",
            "Access consultation history"
        ),
        backendRole = "patient",
        supportsSelfSignup = true,
    ),
    COMMUNITY_HEALTH_WORKER(
        displayName = "Community Health Worker",
        description = "I am a community health worker supporting Nabha residents.",
        starterActions = listOf(
            "Track household outreach visits",
            "Coordinate local health awareness",
            "Escalate high-risk patient cases",
            "Share follow-up reminders"
        ),
        backendRole = "community_health_worker",
        supportsSelfSignup = false,
    ),
    MEDICAL_PROFESSIONAL(
        displayName = "Medical Professional",
        description = "I am a medical professional providing telemedicine services.",
        starterActions = listOf(
            "Review upcoming appointments",
            "Open patient consultation notes",
            "Create and share e-prescriptions",
            "Track completed consultations"
        ),
        backendRole = "doctor",
        supportsSelfSignup = false,
    )
}