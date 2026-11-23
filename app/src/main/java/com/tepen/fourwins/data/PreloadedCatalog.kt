package com.tepen.fourwins.data

import com.tepen.fourwins.model.Challenge30Day
import com.tepen.fourwins.model.Equipment
import com.tepen.fourwins.model.WinHabit
import com.tepen.fourwins.model.WinPillar
import com.tepen.fourwins.model.WorkoutExercise
import com.tepen.fourwins.model.WorkoutProgram

object PreloadedCatalog {

    val defaultHabits: List<WinHabit> = listOf(
        // Physical
        WinHabit("phy_walk", WinPillar.PHYSICAL, "Walk (Treadmill / Outdoor)", 1),
        WinHabit("phy_gym", WinPillar.PHYSICAL, "Gym Session (45–60m)", 2),
        WinHabit("phy_cardio", WinPillar.PHYSICAL, "Running / Low-Impact Row", 3),
        // Mental
        WinHabit("mnt_read", WinPillar.MENTAL, "Read", 1),
        WinHabit("mnt_write", WinPillar.MENTAL, "Write", 2),
        WinHabit("mnt_create", WinPillar.MENTAL, "Creating", 3),
        // Financial
        WinHabit("fin_business", WinPillar.FINANCIAL, "Business", 1),
        WinHabit("fin_working", WinPillar.FINANCIAL, "Working", 2),
        WinHabit("fin_investing", WinPillar.FINANCIAL, "Investing", 3),
        // Spiritual
        WinHabit("spr_gratitude", WinPillar.SPIRITUAL, "Gratitude", 1),
        WinHabit("spr_meditate", WinPillar.SPIRITUAL, "Meditating", 2),
        WinHabit("spr_visual", WinPillar.SPIRITUAL, "Visualization", 3)
    )

    val tailoredPrograms: List<WorkoutProgram> = listOf(
        WorkoutProgram(
            id = "day1_upper_push_core",
            dayNumber = 1,
            title = "Day 1: Upper Push & Core Forge",
            subtitle = "Zero-knee-load strength + Darebee ab finisher",
            category = "Tailored 5-Day Recomp",
            durationMinutes = 50,
            focus = "Chest, Shoulders, Triceps & Core",
            kneeSafeRating = "100% Knee-Safe",
            darebeeSourceRef = "Bench + Dumbbells + 100 Ab Workouts Style",
            exercises = listOf(
                WorkoutExercise(
                    id = "d1_e1",
                    name = "Treadmill Easy Incline Warmup",
                    equipment = Equipment.TREADMILL,
                    sets = 1,
                    repsOrSeconds = "8 min @ 3–4% incline",
                    isTimed = true,
                    durationSeconds = 480,
                    restSeconds = 45,
                    formCue = "Brisk, upright posture, soft heel-to-toe roll. Zero running impact.",
                    kneeSafetyNote = "Low incline warms synovial fluid without joint pounding."
                ),
                WorkoutExercise(
                    id = "d1_e2",
                    name = "Flat Dumbbell Bench Press",
                    equipment = Equipment.EXERCISE_BENCH,
                    sets = 3,
                    repsOrSeconds = "10–12 reps",
                    isTimed = false,
                    restSeconds = 75,
                    formCue = "Keep feet planted lightly on floor, lower dumbbells with 2-sec control.",
                    kneeSafetyNote = "Zero knee torque; do not drive excessively through painful knee."
                ),
                WorkoutExercise(
                    id = "d1_e3",
                    name = "Seated Dumbbell Shoulder Press",
                    equipment = Equipment.DUMBBELLS,
                    sets = 3,
                    repsOrSeconds = "10 reps",
                    isTimed = false,
                    restSeconds = 75,
                    formCue = "Bench upright at 80–90°, brace core, press smoothly overhead without flaring ribs.",
                    kneeSafetyNote = "Seated stability eliminates lower-joint load."
                ),
                WorkoutExercise(
                    id = "d1_e4",
                    name = "Incline Dumbbell Chest Fly",
                    equipment = Equipment.EXERCISE_BENCH,
                    sets = 3,
                    repsOrSeconds = "12 reps",
                    isTimed = false,
                    restSeconds = 60,
                    formCue = "Slight elbow bend, open chest until gentle stretch, squeeze back up.",
                    kneeSafetyNote = "Completely non-weight-bearing for knees."
                ),
                WorkoutExercise(
                    id = "d1_e5",
                    name = "Seated Overhead DB Tricep Extension",
                    equipment = Equipment.DUMBBELLS,
                    sets = 3,
                    repsOrSeconds = "12 reps",
                    isTimed = false,
                    restSeconds = 60,
                    formCue = "Keep elbows tucked near ears, lower smoothly behind head.",
                    kneeSafetyNote = "Seated isolation move."
                ),
                WorkoutExercise(
                    id = "d1_e6",
                    name = "Dead Bug Core Control",
                    equipment = Equipment.BODYWEIGHT,
                    sets = 3,
                    repsOrSeconds = "40 sec",
                    isTimed = true,
                    durationSeconds = 40,
                    restSeconds = 35,
                    formCue = "Press lower back flat against the mat; extend opposite arm & leg slowly.",
                    kneeSafetyNote = "Unloaded supine movement — safe even during mild stiffness."
                ),
                WorkoutExercise(
                    id = "d1_e7",
                    name = "Forearm Plank Hold",
                    equipment = Equipment.BODYWEIGHT,
                    sets = 3,
                    repsOrSeconds = "35 sec hold",
                    isTimed = true,
                    durationSeconds = 35,
                    restSeconds = 40,
                    formCue = "Squeeze glutes and brace abs; keep hips level.",
                    kneeSafetyNote = "Static hold; drop to padded knees if ever needed.",
                    kneeSwapName = "Incline Bench Plank",
                    kneeSwapCue = "Place forearms on the exercise bench to lighten lower-body load."
                )
            )
        ),
        WorkoutProgram(
            id = "day2_cardio_abs",
            dayNumber = 2,
            title = "Day 2: Engine Row & Core Sculpt",
            subtitle = "High calorie burn without joint impact",
            category = "Tailored 5-Day Recomp",
            durationMinutes = 48,
            focus = "Cardio Fat Burn, Back Stamina & Abs",
            kneeSafeRating = "Knee-Protected Cardio",
            darebeeSourceRef = "Rowing + Treadmill + Core Control",
            exercises = listOf(
                WorkoutExercise(
                    id = "d2_e1",
                    name = "Rowing Machine Steady Warmup",
                    equipment = Equipment.ROWING_MACHINE,
                    sets = 1,
                    repsOrSeconds = "6 min easy pace",
                    isTimed = true,
                    durationSeconds = 360,
                    restSeconds = 45,
                    formCue = "Damper level 3–4. Drive gently with heels, sit tall, pull handle to lower ribs.",
                    kneeSafetyNote = "Keep shins vertical at the catch — don't over-compress knees.",
                    kneeSwapName = "Treadmill Flat Brisk Walk",
                    kneeSwapCue = "Walk smoothly at 4.5–5.2 km/h if knee flexion feels tight."
                ),
                WorkoutExercise(
                    id = "d2_e2",
                    name = "Rowing Machine Fat-Burn Intervals",
                    equipment = Equipment.ROWING_MACHINE,
                    sets = 5,
                    repsOrSeconds = "2 min steady / 1 min easy",
                    isTimed = true,
                    durationSeconds = 120,
                    restSeconds = 60,
                    formCue = "22–24 strokes/min controlled power; focus on posture and breathing.",
                    kneeSafetyNote = "Smooth horizontal glide avoids vertical impact shock.",
                    kneeSwapName = "Treadmill Incline Walk Intervals",
                    kneeSwapCue = "Alternate 2 min @ 6% incline with 1 min flat walk."
                ),
                WorkoutExercise(
                    id = "d2_e3",
                    name = "Treadmill Incline Fat-Burn Walk",
                    equipment = Equipment.TREADMILL,
                    sets = 1,
                    repsOrSeconds = "15 min @ 5% incline",
                    isTimed = true,
                    durationSeconds = 900,
                    restSeconds = 45,
                    formCue = "Hands off rails if comfortable; brisk arm swing, conversational pace.",
                    kneeSafetyNote = "Moderate uphill walk engages glutes & hamstrings while reducing knee shear."
                ),
                WorkoutExercise(
                    id = "d2_e4",
                    name = "Seated Bench Knee-Tuck / Flutter",
                    equipment = Equipment.EXERCISE_BENCH,
                    sets = 3,
                    repsOrSeconds = "40 sec",
                    isTimed = true,
                    durationSeconds = 40,
                    restSeconds = 30,
                    formCue = "Sit on edge of bench, lean back slightly, brace abs and extend legs smoothly.",
                    kneeSafetyNote = "No impact on joints."
                ),
                WorkoutExercise(
                    id = "d2_e5",
                    name = "Side Plank Hold (Left & Right)",
                    equipment = Equipment.BODYWEIGHT,
                    sets = 2,
                    repsOrSeconds = "30 sec / side",
                    isTimed = true,
                    durationSeconds = 60,
                    restSeconds = 30,
                    formCue = "Stack shoulders over elbow, lift hips cleanly.",
                    kneeSafetyNote = "Bend bottom knee on pad if lateral knee pressure occurs."
                )
            )
        ),
        WorkoutProgram(
            id = "day3_upper_pull",
            dayNumber = 3,
            title = "Day 3: Upper Pull & Posture Armor",
            subtitle = "Lat pulldown, bench rows, arms & spinal health",
            category = "Tailored 5-Day Recomp",
            durationMinutes = 50,
            focus = "Lats, Upper Back, Biceps & Core",
            kneeSafeRating = "100% Knee-Safe",
            darebeeSourceRef = "Lat Pulldown + Barbell/DB + 100 Ab Workouts",
            exercises = listOf(
                WorkoutExercise(
                    id = "d3_e1",
                    name = "Treadmill Warmup Walk",
                    equipment = Equipment.TREADMILL,
                    sets = 1,
                    repsOrSeconds = "7 min easy brisk walk",
                    isTimed = true,
                    durationSeconds = 420,
                    restSeconds = 45,
                    formCue = "Relax shoulders, warm up entire body before pulling.",
                    kneeSafetyNote = "Flat or 2% incline only."
                ),
                WorkoutExercise(
                    id = "d3_e2",
                    name = "Wide-Grip Lat Pulldown",
                    equipment = Equipment.LAT_PULLDOWN,
                    sets = 4,
                    repsOrSeconds = "10–12 reps",
                    isTimed = false,
                    restSeconds = 75,
                    formCue = "Pull elbows down toward ribs, touch upper chest, pause 1 sec, control up.",
                    kneeSafetyNote = "Adjust thigh pad lightly so it never presses on kneecap."
                ),
                WorkoutExercise(
                    id = "d3_e3",
                    name = "Chest-Supported DB Bench Row",
                    equipment = Equipment.EXERCISE_BENCH,
                    sets = 3,
                    repsOrSeconds = "10–12 reps",
                    isTimed = false,
                    restSeconds = 75,
                    formCue = "Lie chest-down on 30–45° incline bench; row dumbbells toward hips.",
                    kneeSafetyNote = "Bench supports torso — zero lower-back or knee fatigue."
                ),
                WorkoutExercise(
                    id = "d3_e4",
                    name = "Barbell or EZ-Bar Bicep Curl",
                    equipment = Equipment.BARBELLS,
                    sets = 3,
                    repsOrSeconds = "10 reps",
                    isTimed = false,
                    restSeconds = 60,
                    formCue = "Elbows pinned at sides, smooth tempo, no torso swinging.",
                    kneeSafetyNote = "Keep knees soft (not locked), or perform seated DB curls.",
                    kneeSwapName = "Seated Dumbbell Hammer Curl",
                    kneeSwapCue = "Perform seated on bench for zero standing load."
                ),
                WorkoutExercise(
                    id = "d3_e5",
                    name = "Incline Bench Rear-Delt Raise",
                    equipment = Equipment.DUMBBELLS,
                    sets = 3,
                    repsOrSeconds = "12–15 reps",
                    isTimed = false,
                    restSeconds = 60,
                    formCue = "Light dumbbells, sweep arms wide to strengthen posture muscles.",
                    kneeSafetyNote = "Completely knee-neutral."
                ),
                WorkoutExercise(
                    id = "d3_e6",
                    name = "Standing Knee-Safe Oblique Crunch",
                    equipment = Equipment.DUMBBELLS,
                    sets = 3,
                    repsOrSeconds = "12 / side",
                    isTimed = false,
                    restSeconds = 40,
                    formCue = "Hold one light dumbbell, side-bend slowly without twisting spine.",
                    kneeSafetyNote = "Low-load standing core."
                )
            )
        ),
        WorkoutProgram(
            id = "day4_posterior_knee_safe",
            dayNumber = 4,
            title = "Day 4: Knee-Safe Glutes, Hinges & Stamina",
            subtitle = "Strengthen legs around the knee without joint stress",
            category = "Tailored 5-Day Recomp",
            durationMinutes = 48,
            focus = "Glutes, Hamstrings, Calves & Safe Quads",
            kneeSafeRating = "Orthopedic Knee-Safe",
            darebeeSourceRef = "Posterior Chain + Controlled Leg Press",
            exercises = listOf(
                WorkoutExercise(
                    id = "d4_e1",
                    name = "Treadmill Warmup Walk",
                    equipment = Equipment.TREADMILL,
                    sets = 1,
                    repsOrSeconds = "8 min @ 3% incline",
                    isTimed = true,
                    durationSeconds = 480,
                    restSeconds = 45,
                    formCue = "Gentle cadence; check how the knee feels before starting hinges.",
                    kneeSafetyNote = "Stop or lower incline if any sharp joint sensation appears."
                ),
                WorkoutExercise(
                    id = "d4_e2",
                    name = "Floor or Bench Glute Bridge",
                    equipment = Equipment.BODYWEIGHT,
                    sets = 3,
                    repsOrSeconds = "15 reps",
                    isTimed = false,
                    restSeconds = 60,
                    formCue = "Push through heels, squeeze glutes at top for 2 sec. Add DB on hips once easy.",
                    kneeSafetyNote = "Builds hip & hamstring support with minimal patellofemoral stress."
                ),
                WorkoutExercise(
                    id = "d4_e3",
                    name = "Dumbbell Romanian Deadlift (RDL)",
                    equipment = Equipment.DUMBBELLS,
                    sets = 3,
                    repsOrSeconds = "10–12 reps",
                    isTimed = false,
                    restSeconds = 75,
                    formCue = "Soft 15° knee bend ONLY; push hips straight back until hamstring stretch, stand tall.",
                    kneeSafetyNote = "Hip hinge keeps shins nearly vertical — very gentle on knees."
                ),
                WorkoutExercise(
                    id = "d4_e4",
                    name = "Light Controlled Leg Press (Shallow ROM)",
                    equipment = Equipment.LEG_PRESS,
                    sets = 3,
                    repsOrSeconds = "10–12 reps",
                    isTimed = false,
                    restSeconds = 75,
                    formCue = "Feet high & shoulder-width on sled. Lower ONLY to comfortable 45–60° angle, never lock knees.",
                    kneeSafetyNote = "CRITICAL: Keep weight light and stay strictly in pain-free range.",
                    kneeSwapName = "Seated Straight-Leg Quad Raise",
                    kneeSwapCue = "Sit tall on bench, straighten leg & squeeze quad for 3 sec — zero joint compression!"
                ),
                WorkoutExercise(
                    id = "d4_e5",
                    name = "Seated Dumbbell Calf Raise",
                    equipment = Equipment.EXERCISE_BENCH,
                    sets = 3,
                    repsOrSeconds = "15 reps",
                    isTimed = false,
                    restSeconds = 45,
                    formCue = "Dumbbells resting above knees; full stretch and top pause.",
                    kneeSafetyNote = "Seated ankle movement — zero knee flexion strain."
                ),
                WorkoutExercise(
                    id = "d4_e6",
                    name = "Cooldown Treadmill Easy Walk",
                    equipment = Equipment.TREADMILL,
                    sets = 1,
                    repsOrSeconds = "10 min flat easy walk",
                    isTimed = true,
                    durationSeconds = 600,
                    restSeconds = 30,
                    formCue = "Bring heart rate down smoothly.",
                    kneeSafetyNote = "Flushes blood flow around the knee joint."
                )
            )
        ),
        WorkoutProgram(
            id = "day5_fullbody_circuit",
            dayNumber = 5,
            title = "Day 5: Tactical Recomp Circuit",
            subtitle = "Metabolic upper/core + rowing finisher",
            category = "Tailored 5-Day Recomp",
            durationMinutes = 55,
            focus = "Full-Body Conditioning & Calorie Burn",
            kneeSafeRating = "Knee-Safe Circuit",
            darebeeSourceRef = "Minimal Gym + Darebee Conditioning",
            exercises = listOf(
                WorkoutExercise(
                    id = "d5_e1",
                    name = "Rowing Machine Warmup",
                    equipment = Equipment.ROWING_MACHINE,
                    sets = 1,
                    repsOrSeconds = "5 min progressive",
                    isTimed = true,
                    durationSeconds = 300,
                    restSeconds = 45,
                    formCue = "Smooth strokes, keep shins vertical.",
                    kneeSafetyNote = "Use shorter slide if deep knee bend feels uncomfortable.",
                    kneeSwapName = "Treadmill Brisk Walk",
                    kneeSwapCue = "5 min brisk flat walk."
                ),
                WorkoutExercise(
                    id = "d5_e2",
                    name = "Barbell Floor / Bench Press",
                    equipment = Equipment.BARBELLS,
                    sets = 3,
                    repsOrSeconds = "10 reps",
                    isTimed = false,
                    restSeconds = 60,
                    formCue = "Controlled descent to mid-chest, strong press.",
                    kneeSafetyNote = "Zero knee stress."
                ),
                WorkoutExercise(
                    id = "d5_e3",
                    name = "Neutral-Grip Lat Pulldown",
                    equipment = Equipment.LAT_PULLDOWN,
                    sets = 3,
                    repsOrSeconds = "12 reps",
                    isTimed = false,
                    restSeconds = 60,
                    formCue = "Chest tall, drive elbows down smoothly.",
                    kneeSafetyNote = "Zero knee stress."
                ),
                WorkoutExercise(
                    id = "d5_e4",
                    name = "Seated Alternating DB Curl + Press",
                    equipment = Equipment.DUMBBELLS,
                    sets = 3,
                    repsOrSeconds = "10 / arm",
                    isTimed = false,
                    restSeconds = 60,
                    formCue = "Curl cleanly, rotate palms, press overhead.",
                    kneeSafetyNote = "Seated compound movement raises heart rate safely."
                ),
                WorkoutExercise(
                    id = "d5_e5",
                    name = "Darebee-Style Ab Trio (Crunches + Leg Raises + Plank)",
                    equipment = Equipment.BODYWEIGHT,
                    sets = 3,
                    repsOrSeconds = "45 sec work",
                    isTimed = true,
                    durationSeconds = 45,
                    restSeconds = 45,
                    formCue = "Move steadily with strict core tension; rest 45s between rounds.",
                    kneeSafetyNote = "Keep legs slightly bent on lying leg raises to protect lower back."
                ),
                WorkoutExercise(
                    id = "d5_e6",
                    name = "Treadmill Fat-Burn Incline Finisher",
                    equipment = Equipment.TREADMILL,
                    sets = 1,
                    repsOrSeconds = "15 min @ 4–6% incline",
                    isTimed = true,
                    durationSeconds = 900,
                    restSeconds = 30,
                    formCue = "Steady sweat, nose-breathing if possible.",
                    kneeSafetyNote = "No running; controlled incline stride."
                )
            )
        ),
        WorkoutProgram(
            id = "day6_active_recovery",
            dayNumber = 6,
            title = "Day 6–7: Active Recovery & Walk Win",
            subtitle = "Joint lubrication, walking habit & mental reset",
            category = "Tailored 5-Day Recomp",
            durationMinutes = 35,
            focus = "Daily Walk, Mobility & Spiritual Reset",
            kneeSafeRating = "Restorative",
            darebeeSourceRef = "Fitness Over 60 & Daily Movement Style",
            exercises = listOf(
                WorkoutExercise(
                    id = "d6_e1",
                    name = "Outdoor or Treadmill Brisk Walk",
                    equipment = Equipment.TREADMILL,
                    sets = 1,
                    repsOrSeconds = "25 min steady walk",
                    isTimed = true,
                    durationSeconds = 1500,
                    restSeconds = 30,
                    formCue = "Flat smooth pace; aim for 3,000+ steps in one focused block.",
                    kneeSafetyNote = "Gentle rhythmic motion promotes cartilage nutrition."
                ),
                WorkoutExercise(
                    id = "d6_e2",
                    name = "Cat-Cow & Thoracic Mobility",
                    equipment = Equipment.BODYWEIGHT,
                    sets = 2,
                    repsOrSeconds = "45 sec",
                    isTimed = true,
                    durationSeconds = 45,
                    restSeconds = 20,
                    formCue = "Move vertebra by vertebra with deep breaths.",
                    kneeSafetyNote = "Place folded mat/towel under knees or do seated spine twists.",
                    kneeSwapName = "Seated Chair Cat-Cow",
                    kneeSwapCue = "Sit tall on bench and arch/round upper back cleanly."
                ),
                WorkoutExercise(
                    id = "d6_e3",
                    name = "1-Minute Box Breathing & Gratitude",
                    equipment = Equipment.BODYWEIGHT,
                    sets = 1,
                    repsOrSeconds = "60 sec",
                    isTimed = true,
                    durationSeconds = 60,
                    restSeconds = 0,
                    formCue = "4s inhale, 4s hold, 4s exhale, 4s hold. Reflect on 1 win today.",
                    kneeSafetyNote = "Links directly to Spiritual Win!"
                )
            )
        )
    )

    val expressWorkouts: List<WorkoutProgram> = listOf(
        WorkoutProgram(
            id = "darebee_2min_abs",
            dayNumber = null,
            title = "2-Minute Abs Express",
            subtitle = "Zero-equipment fast core circuit",
            category = "Darebee PDF Express",
            durationMinutes = 8,
            focus = "Core & Waistline",
            kneeSafeRating = "100% Knee-Safe",
            darebeeSourceRef = "100 Ab Workouts by Darebee",
            exercises = listOf(
                WorkoutExercise(
                    id = "ab1",
                    name = "Seated Knee-Safe Bicycle / Flutter",
                    equipment = Equipment.BODYWEIGHT,
                    sets = 3,
                    repsOrSeconds = "30 sec",
                    isTimed = true,
                    durationSeconds = 30,
                    restSeconds = 20,
                    formCue = "Keep lower back pressed down; slow controlled reps.",
                    kneeSafetyNote = "Unloaded supine core."
                ),
                WorkoutExercise(
                    id = "ab2",
                    name = "Crunches (Hands Across Chest)",
                    equipment = Equipment.BODYWEIGHT,
                    sets = 3,
                    repsOrSeconds = "30 sec",
                    isTimed = true,
                    durationSeconds = 30,
                    restSeconds = 20,
                    formCue = "Lift shoulder blades off floor, exhale on crunch.",
                    kneeSafetyNote = "Feet flat on floor at comfortable knee angle."
                ),
                WorkoutExercise(
                    id = "ab3",
                    name = "Forearm Plank Hold",
                    equipment = Equipment.BODYWEIGHT,
                    sets = 3,
                    repsOrSeconds = "30 sec",
                    isTimed = true,
                    durationSeconds = 30,
                    restSeconds = 30,
                    formCue = "Keep ribs tucked and glutes engaged.",
                    kneeSafetyNote = "Switch to bench incline plank anytime."
                )
            )
        ),
        WorkoutProgram(
            id = "darebee_standing_abs",
            dayNumber = null,
            title = "Standing Abs & Posture Reset",
            subtitle = "No floor needed — great between work blocks",
            category = "Darebee PDF Express",
            durationMinutes = 12,
            focus = "Obliques, Deep Core & Balance",
            kneeSafeRating = "100% Knee-Safe",
            darebeeSourceRef = "100 Ab Workouts — Standing Abs",
            exercises = listOf(
                WorkoutExercise(
                    id = "sa1",
                    name = "Standing Cross-Body Elbow-to-Knee (Gentle)",
                    equipment = Equipment.BODYWEIGHT,
                    sets = 3,
                    repsOrSeconds = "40 sec",
                    isTimed = true,
                    durationSeconds = 40,
                    restSeconds = 20,
                    formCue = "Slow deliberate march, twist torso to meet raised knee comfortably.",
                    kneeSafetyNote = "No hopping; keep standing knee slightly soft."
                ),
                WorkoutExercise(
                    id = "sa2",
                    name = "Standing Torso Twist + Overhead Reach",
                    equipment = Equipment.BODYWEIGHT,
                    sets = 3,
                    repsOrSeconds = "40 sec",
                    isTimed = true,
                    durationSeconds = 40,
                    restSeconds = 20,
                    formCue = "Brace abs tight while rotating smoothly.",
                    kneeSafetyNote = "Zero joint impact."
                ),
                WorkoutExercise(
                    id = "sa3",
                    name = "Shadow Boxing Jab-Cross Combo",
                    equipment = Equipment.BODYWEIGHT,
                    sets = 3,
                    repsOrSeconds = "45 sec",
                    isTimed = true,
                    durationSeconds = 45,
                    restSeconds = 25,
                    formCue = "Fast crisp punches, rotate hips gently, keep shoulders active.",
                    kneeSafetyNote = "Fun cardio burst with zero jumping."
                )
            )
        ),
        WorkoutProgram(
            id = "darebee_db_upper_blast",
            dayNumber = null,
            title = "Dumbbell & Bench 20-Min Blast",
            subtitle = "Compact strength session for busy days",
            category = "Darebee PDF Express",
            durationMinutes = 20,
            focus = "Upper Body & Arms",
            kneeSafeRating = "100% Knee-Safe",
            darebeeSourceRef = "100 Dumbbell Workouts Style",
            exercises = listOf(
                WorkoutExercise(
                    id = "db1",
                    name = "Dumbbell Floor / Bench Press",
                    equipment = Equipment.DUMBBELLS,
                    sets = 4,
                    repsOrSeconds = "12 reps",
                    isTimed = false,
                    restSeconds = 45,
                    formCue = "Consistent pace, full chest squeeze.",
                    kneeSafetyNote = "Zero knee stress."
                ),
                WorkoutExercise(
                    id = "db2",
                    name = "Bent-Over or Bench-Supported DB Row",
                    equipment = Equipment.DUMBBELLS,
                    sets = 4,
                    repsOrSeconds = "12 reps",
                    isTimed = false,
                    restSeconds = 45,
                    formCue = "Squeeze shoulder blades back.",
                    kneeSafetyNote = "Support with one knee or chest on bench."
                ),
                WorkoutExercise(
                    id = "db3",
                    name = "Seated DB Arnold Press",
                    equipment = Equipment.DUMBBELLS,
                    sets = 3,
                    repsOrSeconds = "10 reps",
                    isTimed = false,
                    restSeconds = 45,
                    formCue = "Rotate palms smoothly as you press up.",
                    kneeSafetyNote = "Seated."
                )
            )
        )
    )

    val challenges30Day: List<Challenge30Day> = listOf(
        Challenge30Day(
            id = "ch_meditation_1min",
            title = "30-Day 1-Min Meditation",
            subtitle = "Daily stillness before checking your phone",
            darebeeRef = "100 Fitness Challenges #1",
            linkedPillar = WinPillar.SPIRITUAL,
            kneeSafe = true,
            dailyTargets = List(30) { day -> "Day ${day + 1}: ${1 + (day / 10)} min quiet focus & deep breathing" }
        ),
        Challenge30Day(
            id = "ch_walk_streak",
            title = "30-Day Incline & Walk Streak",
            subtitle = "Knee-friendly fat-burn daily step progression",
            darebeeRef = "Daily Movement Habit",
            linkedPillar = WinPillar.PHYSICAL,
            kneeSafe = true,
            dailyTargets = List(30) { day ->
                val mins = 15 + ((day / 5) * 3)
                "Day ${day + 1}: $mins min brisk flat or 3–5% incline walk"
            }
        ),
        Challenge30Day(
            id = "ch_core_control",
            title = "30-Day Core Control (Plank)",
            subtitle = "Build deep core stability safely from 20s to 90s",
            darebeeRef = "100 Fitness Challenges #2 & #36",
            linkedPillar = WinPillar.PHYSICAL,
            kneeSafe = true,
            dailyTargets = List(30) { day ->
                val sec = 20 + (day * 2)
                if ((day + 1) % 5 == 0) "Day ${day + 1}: Active Recovery — 40s Dead Bug"
                else "Day ${day + 1}: ${sec}s Forearm or Bench Plank (split sets OK)"
            }
        ),
        Challenge30Day(
            id = "ch_gratitude_win",
            title = "30-Day Daily Gratitude & Wins",
            subtitle = "Log one specific win every evening before sleep",
            darebeeRef = "100 Fitness Challenges #37 — Daily Gratitude",
            linkedPillar = WinPillar.SPIRITUAL,
            kneeSafe = true,
            dailyTargets = List(30) { day -> "Day ${day + 1}: Name 1–3 concrete wins & write 1 gratitude line" }
        )
    )

    data class OfficialDarebeeBookRef(
        val title: String,
        val subtitle: String,
        val pageUrl: String,
        val kneeFriendlyTag: String
    )

    val officialBooksDirectory: List<OfficialDarebeeBookRef> = listOf(
        OfficialDarebeeBookRef(
            title = "100 Ab Workouts by DAREBEE",
            subtitle = "100 bodyweight core routines (many floor/standing knee-safe)",
            pageUrl = "https://darebee.com/abs-100-workouts.html",
            kneeFriendlyTag = "Mostly Knee-Safe"
        ),
        OfficialDarebeeBookRef(
            title = "100 Dumbbell Workouts",
            subtitle = "100 dumbbell routines matching your home/gym dumbbells",
            pageUrl = "https://darebee.com/100-dumbbell-workouts.html",
            kneeFriendlyTag = "Modify Leg Moves"
        ),
        OfficialDarebeeBookRef(
            title = "100 Fitness Challenges",
            subtitle = "30-day physical & mental challenges (Meditation, Plank, Gratitude)",
            pageUrl = "https://darebee.com/100-fitness-challenges.html",
            kneeFriendlyTag = "Filter Jumping Days"
        ),
        OfficialDarebeeBookRef(
            title = "Fitness Over 60 (Low-Impact)",
            subtitle = "Joint-gentle mobility & strength routines ideal for knee recovery",
            pageUrl = "https://darebee.com/fitness-over-60.html",
            kneeFriendlyTag = "Top Knee-Safe Pick"
        )
    )
}
