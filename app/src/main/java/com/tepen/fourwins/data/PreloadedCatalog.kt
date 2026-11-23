package com.tepen.fourwins.data

import com.tepen.fourwins.model.Challenge30Day
import com.tepen.fourwins.model.Equipment
import com.tepen.fourwins.model.ExerciseMotionType
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
                    kneeSafetyNote = "Low incline warms synovial fluid without joint pounding.",
                    motionType = ExerciseMotionType.WALK_INCLINE,
                    steps = listOf(
                        "Set treadmill speed to 4.2–5.0 km/h and incline to 3–4%.",
                        "Stand tall with chest open, gaze forward (don't stare down at feet).",
                        "Land softly mid-heel and roll smoothly through the toes with relaxed knees.",
                        "Swing arms naturally at 90° without gripping the side rails."
                    ),
                    breathingCue = "Steady nasal or 3-step inhale / 3-step exhale rhythm.",
                    commonMistakes = "Overstriding with locked knees or leaning heavily on the handrails.",
                    videoQuery = "treadmill incline walk proper form posture"
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
                    kneeSafetyNote = "Zero knee torque; do not drive excessively through painful knee.",
                    motionType = ExerciseMotionType.BENCH_PRESS,
                    steps = listOf(
                        "Sit on the flat bench with dumbbells on thighs, then lie back while bringing weights over chest.",
                        "Pin shoulder blades gently down and together against the bench pad.",
                        "Lower dumbbells slowly (2–3 sec) to outer chest level with elbows tucked ~45° from torso.",
                        "Press smoothly back up until arms are extended without slamming weights together."
                    ),
                    breathingCue = "Inhale as you lower dumbbells toward chest; exhale strongly as you press up.",
                    commonMistakes = "Flaring elbows straight out to 90° or bouncing dumbbells off the chest.",
                    videoQuery = "flat dumbbell bench press proper form tutorial"
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
                    kneeSafetyNote = "Seated stability eliminates lower-joint load.",
                    motionType = ExerciseMotionType.SHOULDER_PRESS,
                    steps = listOf(
                        "Set bench backrest to 80–85° upright; rest back firmly against the pad.",
                        "Bring dumbbells to ear/shoulder height with palms facing forward and elbows slightly in front of body.",
                        "Brace your abs so lower back stays neutral, then press dumbbells up in a slight arc overhead.",
                        "Lower under 2-second control back to ear level."
                    ),
                    breathingCue = "Inhale at shoulder height; exhale on the overhead drive.",
                    commonMistakes = "Arching lower back off the bench or locking elbows aggressively at the top.",
                    videoQuery = "seated dumbbell shoulder press step by step form"
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
                    kneeSafetyNote = "Completely non-weight-bearing for knees.",
                    motionType = ExerciseMotionType.CHEST_FLY,
                    steps = listOf(
                        "Set bench to a gentle 30° incline and hold lighter dumbbells overhead with palms facing each other.",
                        "Maintain a soft 15–20° bend in your elbows throughout the entire set.",
                        "Open arms out in a wide arc until dumbbells are level with upper chest and you feel a clean stretch.",
                        "Hug a wide barrel back to the starting position by squeezing chest muscles."
                    ),
                    breathingCue = "Deep inhale while opening arms wide; exhale as you bring hands together.",
                    commonMistakes = "Dropping arms too deep below shoulder level or turning the fly into a press.",
                    videoQuery = "incline dumbbell chest fly proper form"
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
                    kneeSafetyNote = "Seated isolation move.",
                    motionType = ExerciseMotionType.TRICEP_EXT,
                    steps = listOf(
                        "Sit upright on bench and grip one dumbbell by the top plate with both hands.",
                        "Raise it straight overhead with elbows pointed forward and close to your temples.",
                        "Keep upper arms still while bending elbows to lower the dumbbell behind your head.",
                        "Extend elbows smoothly back to overhead and squeeze triceps at the top."
                    ),
                    breathingCue = "Inhale lowering behind head; exhale extending up.",
                    commonMistakes = "Letting elbows flare wide or overarching the lower back.",
                    videoQuery = "seated overhead dumbbell tricep extension form"
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
                    kneeSafetyNote = "Unloaded supine movement — safe even during mild stiffness.",
                    motionType = ExerciseMotionType.DEAD_BUG,
                    steps = listOf(
                        "Lie flat on your back with arms pointed straight to ceiling and knees bent 90° above hips.",
                        "Tilt pelvis slightly so your lower back presses flush into the mat (no gap under spine).",
                        "Slowly extend right arm overhead and left leg toward the floor without letting lower back lift.",
                        "Return to center and switch sides smoothly."
                    ),
                    breathingCue = "Long exhale as opposite arm and leg extend; inhale returning to center.",
                    commonMistakes = "Lower back arching off the floor or dropping the leg too fast.",
                    videoQuery = "dead bug exercise proper form darebee"
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
                    kneeSwapCue = "Place forearms on the exercise bench to lighten lower-body load.",
                    motionType = ExerciseMotionType.PLANK,
                    steps = listOf(
                        "Place forearms on mat (or bench) with elbows directly under shoulders.",
                        "Step feet back hip-width apart and form a straight line from ears through heels.",
                        "Squeeze glutes, pull belly button gently toward spine, and push floor away with forearms.",
                        "Hold steady without letting hips sag or pike upward."
                    ),
                    breathingCue = "Slow, steady breaths — never hold your breath during planks.",
                    commonMistakes = "Hips sagging toward the floor or craning neck up.",
                    videoQuery = "forearm plank proper form darebee"
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
                    kneeSwapCue = "Walk smoothly at 4.5–5.2 km/h if knee flexion feels tight.",
                    motionType = ExerciseMotionType.ROWING,
                    steps = listOf(
                        "Set damper between 3 and 4 and strap feet securely across the widest part of the shoe.",
                        "At the catch (front), keep torso tilted slightly forward and shins vertical — stop before knees jam.",
                        "Drive sequence: 1) Push with legs, 2) Hinge torso slightly back, 3) Pull handle to lower ribs.",
                        "Recovery sequence (reverse): 1) Arms extend, 2) Torso hinges forward, 3) Knees bend smoothly."
                    ),
                    breathingCue = "Exhale on the pull drive; inhale during the smooth forward recovery.",
                    commonMistakes = "Pulling with arms before legs finish pushing, or hunching over at the front.",
                    videoQuery = "rowing machine proper technique form legs body arms"
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
                    kneeSwapCue = "Alternate 2 min @ 6% incline with 1 min flat walk.",
                    motionType = ExerciseMotionType.ROWING,
                    steps = listOf(
                        "Row 2 minutes at a strong, crisp 22–24 strokes/min (focus on power per stroke, not frantic speed).",
                        "Keep shoulders relaxed down away from ears on every pull.",
                        "During the 1-minute recovery, keep gliding gently with light leg pressure to flush fatigue.",
                        "Monitor knee angle at the catch — keep shin vertical and pain-free."
                    ),
                    breathingCue = "Two-breath pattern or crisp exhale on every pull.",
                    commonMistakes = "Rushing the slide forward or bending knees before hands clear them.",
                    videoQuery = "rowing machine interval workout form"
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
                    kneeSafetyNote = "Moderate uphill walk engages glutes & hamstrings while reducing knee shear.",
                    motionType = ExerciseMotionType.WALK_INCLINE,
                    steps = listOf(
                        "Set treadmill incline to 4–6% and pace to 4.5–5.3 km/h.",
                        "Lean slightly forward from the ankles (not the waist) to match the slope.",
                        "Push off through glutes and calves rather than stomping the front foot.",
                        "Keep pace steady enough to break a sweat while still able to speak short sentences."
                    ),
                    breathingCue = "Deep rhythmic breathing; slow down slightly if breathing becomes ragged.",
                    commonMistakes = "Hanging onto the front console and leaning backward (kills incline benefit).",
                    videoQuery = "incline treadmill walk fat loss proper posture"
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
                    kneeSafetyNote = "No impact on joints.",
                    motionType = ExerciseMotionType.CRUNCH_CORE,
                    steps = listOf(
                        "Sit sideways or on the end of the exercise bench, gripping the edges lightly behind hips.",
                        "Lean torso back ~30–40° with chest proud and lower abs braced.",
                        "Lift feet slightly off floor and alternate controlled knee tucks or straight-leg flutters.",
                        "Move from the core rather than swinging the torso."
                    ),
                    breathingCue = "Steady short breaths; exhale as knees draw toward chest.",
                    commonMistakes = "Rounding lower back or gripping bench so hard that shoulders hunch.",
                    videoQuery = "seated bench knee tucks abs form"
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
                    kneeSafetyNote = "Bend bottom knee on pad if lateral knee pressure occurs.",
                    motionType = ExerciseMotionType.PLANK,
                    steps = listOf(
                        "Lie on your side with forearm flat and elbow directly beneath shoulder.",
                        "Stack feet (or bend bottom knee 90° on a padded mat for extra knee comfort).",
                        "Lift hips off the floor until torso and legs form a straight diagonal line.",
                        "Hold 30 seconds, then repeat cleanly on the opposite side."
                    ),
                    breathingCue = "Smooth, controlled breaths while keeping obliques tight.",
                    commonMistakes = "Hips drifting backward or top shoulder rolling forward.",
                    videoQuery = "side plank hold proper form darebee"
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
                    kneeSafetyNote = "Flat or 2% incline only.",
                    motionType = ExerciseMotionType.WALK_INCLINE,
                    steps = listOf(
                        "Start at a gentle 4.0 km/h and build smoothly to 4.8 km/h.",
                        "Roll shoulders backward gently during minute 2–3 to loosen upper back.",
                        "Keep stride light and natural."
                    ),
                    breathingCue = "Relaxed nasal breathing.",
                    commonMistakes = "Starting too fast before joints warm up.",
                    videoQuery = "treadmill warmup walk form"
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
                    kneeSafetyNote = "Adjust thigh pad lightly so it never presses on kneecap.",
                    motionType = ExerciseMotionType.LAT_PULLDOWN,
                    steps = listOf(
                        "Set thigh pad comfortably over mid-thigh (never touching kneecap) and grip bar slightly wider than shoulders.",
                        "Sit tall with a slight 10–15° lean back and depress shoulder blades before bending elbows.",
                        "Drive elbows straight down toward your pockets until the bar reaches upper chest / collarbone.",
                        "Pause 1 second squeezing lats, then raise bar under 2-second control to full stretch."
                    ),
                    breathingCue = "Exhale as you pull the bar down; inhale as arms extend overhead.",
                    commonMistakes = "Swinging torso way back to use momentum or pulling bar behind the neck.",
                    videoQuery = "lat pulldown proper form step by step"
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
                    kneeSafetyNote = "Bench supports torso — zero lower-back or knee fatigue.",
                    motionType = ExerciseMotionType.BENT_ROW,
                    steps = listOf(
                        "Set bench to 30–45° incline and lie chest-down with feet resting gently on the floor behind you.",
                        "Let dumbbells hang straight down with neutral palms facing each other.",
                        "Pull elbows up and back toward your hips (not straight to shoulders) while squeezing shoulder blades.",
                        "Pause 1 second at top, then lower smoothly to full hang."
                    ),
                    breathingCue = "Exhale as you row dumbbells up; inhale on the controlled lower.",
                    commonMistakes = "Shrugging shoulders toward ears or lifting chest off the bench.",
                    videoQuery = "chest supported dumbbell row proper form"
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
                    kneeSwapCue = "Perform seated on bench for zero standing load.",
                    motionType = ExerciseMotionType.BICEP_CURL,
                    steps = listOf(
                        "Hold barbell or dumbbells at thigh level with shoulder-width underhand grip.",
                        "Pin upper arms alongside your ribs and stand tall with knees unlocked.",
                        "Curl weight smoothly up toward front shoulders without swinging hips.",
                        "Lower slowly over 2–3 seconds to full extension."
                    ),
                    breathingCue = "Exhale curling up; inhale lowering down.",
                    commonMistakes = "Throwing elbows forward or leaning back to hoist heavy weight.",
                    videoQuery = "barbell bicep curl proper form tutorial"
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
                    kneeSafetyNote = "Completely knee-neutral.",
                    motionType = ExerciseMotionType.BENT_ROW,
                    steps = listOf(
                        "Lie chest-down on a 30° incline bench with light dumbbells hanging beneath chest.",
                        "Keep a slight bend in elbows and raise arms straight out to the sides in a 'T' shape.",
                        "Focus on the backs of your shoulders rather than squeezing traps up.",
                        "Lower slowly and repeat cleanly."
                    ),
                    breathingCue = "Exhale sweeping wide; inhale lowering.",
                    commonMistakes = "Using weights that are too heavy and swinging with momentum.",
                    videoQuery = "chest supported rear delt fly proper form"
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
                    kneeSafetyNote = "Low-load standing core.",
                    motionType = ExerciseMotionType.STANDING_CORE,
                    steps = listOf(
                        "Stand tall with feet hip-width apart holding one dumbbell in your right hand.",
                        "Place left fingertips gently behind ear or on hip.",
                        "Bend straight sideways to the right down your thigh, then contract left obliques to stand upright.",
                        "Complete all reps on one side, then switch hands."
                    ),
                    breathingCue = "Inhale leaning sideways; exhale pulling upright.",
                    commonMistakes = "Holding dumbbells in both hands (they cancel out!) or leaning forward.",
                    videoQuery = "dumbbell side bend oblique exercise proper form"
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
                    kneeSafetyNote = "Stop or lower incline if any sharp joint sensation appears.",
                    motionType = ExerciseMotionType.WALK_INCLINE,
                    steps = listOf(
                        "Walk at an easy 4.0–4.6 km/h at 2–3% incline for 8 minutes.",
                        "Pay attention to knee tracking — toes pointing straight ahead.",
                        "Finish feeling warm and lubricated, not fatigued."
                    ),
                    breathingCue = "Calm, even breathing.",
                    commonMistakes = "Skipping warmup before lower-body work.",
                    videoQuery = "knee friendly treadmill warmup walk"
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
                    kneeSafetyNote = "Builds hip & hamstring support with minimal patellofemoral stress.",
                    motionType = ExerciseMotionType.GLUTE_BRIDGE,
                    steps = listOf(
                        "Lie on your back with knees bent and feet flat on floor positioned so shins are vertical at top.",
                        "Brace your core slightly (ribs down) so lower back doesn't hyperextend.",
                        "Drive through heels to lift hips until shoulders, hips, and knees align.",
                        "Squeeze glutes hard for 2 seconds at top, then lower slowly."
                    ),
                    breathingCue = "Exhale driving hips up; inhale lowering back to floor.",
                    commonMistakes = "Placing feet too close to glutes (increases knee bend) or arching lower back.",
                    videoQuery = "glute bridge proper form darebee"
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
                    kneeSafetyNote = "Hip hinge keeps shins nearly vertical — very gentle on knees.",
                    motionType = ExerciseMotionType.ROMANIAN_DEADLIFT,
                    steps = listOf(
                        "Stand holding dumbbells in front of thighs with feet hip-width and a soft ~15° knee unlock.",
                        "Lock that slight knee angle in place and push your hips straight backward as if closing a car door.",
                        "Slide dumbbells close down the fronts of your thighs until mid-shin (flat back, chest proud).",
                        "Drive hips forward by squeezing glutes and hamstrings to stand tall."
                    ),
                    breathingCue = "Inhale hinging hips back; exhale standing tall.",
                    commonMistakes = "Turning the RDL into a squat by bending knees forward, or rounding the lower back.",
                    videoQuery = "dumbbell romanian deadlift proper form step by step"
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
                    kneeSwapCue = "Sit tall on bench, straighten leg & squeeze quad for 3 sec — zero joint compression!",
                    motionType = ExerciseMotionType.LEG_PRESS_SHALLOW,
                    steps = listOf(
                        "Place feet HIGH and shoulder-width on the leg press platform (high placement reduces knee bend).",
                        "Unlock safety handles with knees slightly soft — NEVER snap knees into a hard lockout.",
                        "Lower sled smoothly ONLY to a comfortable 45–60° knee angle (stop well before deep fold).",
                        "Press smoothly through mid-foot and heel back up to soft-knee start."
                    ),
                    breathingCue = "Inhale lowering sled; exhale pressing up.",
                    commonMistakes = "Letting knees cave inward, lowering too deep, or locking knees straight at top.",
                    videoQuery = "knee friendly leg press foot placement shallow range"
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
                    kneeSafetyNote = "Seated ankle movement — zero knee flexion strain.",
                    motionType = ExerciseMotionType.CALF_RAISE,
                    steps = listOf(
                        "Sit upright on bench with balls of feet on floor (or a thin plate) and dumbbells resting on lower thighs.",
                        "Push through big toes to raise heels as high as possible and pause 1 full second.",
                        "Lower heels slowly all the way down for a clean stretch."
                    ),
                    breathingCue = "Exhale pressing up; inhale lowering.",
                    commonMistakes = "Bouncing rapidly at the bottom without pausing at peak contraction.",
                    videoQuery = "seated dumbbell calf raise form"
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
                    kneeSafetyNote = "Flushes blood flow around the knee joint.",
                    motionType = ExerciseMotionType.WALK_INCLINE,
                    steps = listOf(
                        "Set treadmill flat (0% incline) and easy 3.8–4.2 km/h pace.",
                        "Let heart rate settle and take deep recovery breaths."
                    ),
                    breathingCue = "4-second inhale, 6-second slow exhale.",
                    commonMistakes = "Stepping straight off after leg work without cooling down.",
                    videoQuery = "treadmill cooldown walk recovery"
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
                    kneeSwapCue = "5 min brisk flat walk.",
                    motionType = ExerciseMotionType.ROWING,
                    steps = listOf(
                        "Start gently at 18–20 strokes/min for minutes 1–3, then build to 22 strokes/min.",
                        "Focus on tall posture and smooth leg-body-arm sequencing."
                    ),
                    breathingCue = "Exhale on drive, inhale on recovery.",
                    commonMistakes = "Pulling too aggressively on the first 10 strokes.",
                    videoQuery = "rowing machine warmup proper form"
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
                    kneeSafetyNote = "Zero knee stress.",
                    motionType = ExerciseMotionType.BENCH_PRESS,
                    steps = listOf(
                        "Lie under barbell with eyes aligned under the bar and grip just outside shoulder width.",
                        "Unrack bar smoothly over chest with shoulder blades pinned down to the bench.",
                        "Lower bar under 2-sec control to touch mid-chest lightly with elbows tucked ~45°.",
                        "Press smoothly up and slightly back toward shoulder line."
                    ),
                    breathingCue = "Inhale lowering to chest; exhale pressing up.",
                    commonMistakes = "Bouncing bar off ribs or lifting hips off bench.",
                    videoQuery = "barbell bench press proper form step by step"
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
                    kneeSafetyNote = "Zero knee stress.",
                    motionType = ExerciseMotionType.LAT_PULLDOWN,
                    steps = listOf(
                        "Attach close neutral-grip handle (palms facing each other) and sit upright.",
                        "Pull elbows straight down beside ribcage until handle reaches upper chest.",
                        "Control smoothly back up to full overhead arm stretch."
                    ),
                    breathingCue = "Exhale pulling down; inhale extending up.",
                    commonMistakes = "Leaning excessively backward or shrugging shoulders.",
                    videoQuery = "neutral grip lat pulldown form"
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
                    kneeSafetyNote = "Seated compound movement raises heart rate safely.",
                    motionType = ExerciseMotionType.SHOULDER_PRESS,
                    steps = listOf(
                        "Sit tall on upright bench holding dumbbells at your sides.",
                        "Curl left dumbbell to shoulder, rotate palm forward, and press straight overhead.",
                        "Lower smoothly to shoulder, then back to side, and immediately repeat with right arm."
                    ),
                    breathingCue = "Exhale on each overhead press; breathe rhythmically between arms.",
                    commonMistakes = "Swinging torso sideways to assist the curl.",
                    videoQuery = "seated dumbbell curl and press form"
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
                    kneeSafetyNote = "Keep legs slightly bent on lying leg raises to protect lower back.",
                    motionType = ExerciseMotionType.CRUNCH_CORE,
                    steps = listOf(
                        "Round flow (15s each): 15s controlled Crunches → 15s Lying Bent-Knee Leg Raises → 15s Forearm Plank.",
                        "Keep lower back pressed against mat during leg raises (place hands under hips if helpful).",
                        "Transition smoothly without rushing."
                    ),
                    breathingCue = "Exhale on every crunch and leg lift; breathe steadily in plank.",
                    commonMistakes = "Pulling on neck during crunches or arching lower back on leg lowers.",
                    videoQuery = "darebee ab workout crunches leg raises plank form"
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
                    kneeSafetyNote = "No running; controlled incline stride.",
                    motionType = ExerciseMotionType.WALK_INCLINE,
                    steps = listOf(
                        "Walk steadily at 4–6% incline at a brisk 4.6–5.2 km/h.",
                        "Finish the final 2 minutes at 0% flat incline to cool down."
                    ),
                    breathingCue = "Steady deep breathing.",
                    commonMistakes = "Slouching shoulders when tired.",
                    videoQuery = "incline treadmill finisher walking form"
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
                    kneeSafetyNote = "Gentle rhythmic motion promotes cartilage nutrition.",
                    motionType = ExerciseMotionType.WALK_INCLINE,
                    steps = listOf(
                        "Walk outdoors or on a flat treadmill at a comfortable brisk pace.",
                        "Keep shoulders relaxed, arms swinging lightly, and stride smooth."
                    ),
                    breathingCue = "Relaxed rhythmic breathing.",
                    commonMistakes = "Pushing speed into pain on a recovery day.",
                    videoQuery = "brisk walking posture technique"
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
                    kneeSwapCue = "Sit tall on bench and arch/round upper back cleanly.",
                    motionType = ExerciseMotionType.MOBILITY_BREATH,
                    steps = listOf(
                        "Sit on bench edge (or kneel on thick pad) with hands on knees or mat.",
                        "Inhale: lift chest forward and gently arch mid-back (Cow).",
                        "Exhale: tuck chin, pull belly button in, and round upper back toward ceiling (Cat)."
                    ),
                    breathingCue = "Inhale to open chest; exhale to round spine.",
                    commonMistakes = "Forcing neck backward instead of moving through upper/mid spine.",
                    videoQuery = "cat cow stretch darebee form"
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
                    kneeSafetyNote = "Links directly to Spiritual Win!",
                    motionType = ExerciseMotionType.MOBILITY_BREATH,
                    steps = listOf(
                        "Sit comfortably with spine tall and eyes softly closed.",
                        "Inhale gently through nose for 4 seconds.",
                        "Hold smoothly for 4 seconds without tension.",
                        "Exhale slowly for 4 seconds, pause 4 seconds empty, and name 1 gratitude win."
                    ),
                    breathingCue = "4-4-4-4 Box Breathing cadence.",
                    commonMistakes = "Forcing huge breaths that cause dizziness; keep breaths gentle.",
                    videoQuery = "box breathing 4 4 4 4 technique"
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
                    kneeSafetyNote = "Unloaded supine core.",
                    motionType = ExerciseMotionType.DEAD_BUG,
                    steps = listOf(
                        "Lie flat on your back with hands lightly behind head or resting by hips.",
                        "Press lower back into the mat and elevate legs to ~45°.",
                        "Perform slow alternating flutter kicks or gentle bicycle reaches without yanking neck."
                    ),
                    breathingCue = "Continuous steady exhales — do not hold breath.",
                    commonMistakes = "Lower back lifting off mat (raise legs higher if that happens).",
                    videoQuery = "flutter kicks proper form darebee"
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
                    kneeSafetyNote = "Feet flat on floor at comfortable knee angle.",
                    motionType = ExerciseMotionType.CRUNCH_CORE,
                    steps = listOf(
                        "Lie on back with knees comfortably bent, feet flat, arms crossed over chest.",
                        "Curl ribs toward hips until shoulder blades lift cleanly off the mat.",
                        "Pause 1 beat at top, then lower slowly without dropping head."
                    ),
                    breathingCue = "Exhale curling up; inhale lowering down.",
                    commonMistakes = "Using hip momentum or lifting all the way into a full sit-up.",
                    videoQuery = "crunches proper form darebee"
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
                    kneeSafetyNote = "Switch to bench incline plank anytime.",
                    motionType = ExerciseMotionType.PLANK,
                    steps = listOf(
                        "Align elbows directly under shoulders on the mat.",
                        "Brace abs, squeeze glutes, and hold straight torso line for 30 seconds."
                    ),
                    breathingCue = "Slow, steady breaths.",
                    commonMistakes = "Hips sagging or shrugging shoulders.",
                    videoQuery = "forearm plank proper form darebee"
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
                    kneeSafetyNote = "No hopping; keep standing knee slightly soft.",
                    motionType = ExerciseMotionType.STANDING_CORE,
                    steps = listOf(
                        "Stand tall with feet shoulder-width apart and fingertips lightly behind ears.",
                        "Lift right knee comfortably to hip height (never force past comfort) while rotating left elbow across.",
                        "Lower foot softly and alternate sides smoothly."
                    ),
                    breathingCue = "Exhale on every cross-body twist.",
                    commonMistakes = "Hunching forward instead of twisting through the obliques.",
                    videoQuery = "standing cross body crunch darebee"
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
                    kneeSafetyNote = "Zero joint impact.",
                    motionType = ExerciseMotionType.STANDING_CORE,
                    steps = listOf(
                        "Stand tall with knees unlocked and core braced.",
                        "Reach overhead, then rotate torso crisply left and right from the ribcage while keeping hips square."
                    ),
                    breathingCue = "Quick exhale on each side rotation.",
                    commonMistakes = "Twisting planted knees aggressively — pivot feet slightly if rotating wide.",
                    videoQuery = "standing torso twists darebee"
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
                    kneeSafetyNote = "Fun cardio burst with zero jumping.",
                    motionType = ExerciseMotionType.SHADOW_BOXING,
                    steps = listOf(
                        "Take a comfortable staggered boxing stance with knees soft and fists guarding your chin.",
                        "Snap lead hand straight out (Jab), bring it straight back to chin.",
                        "Rotate rear shoulder/hip gently to snap rear hand straight out (Cross), then reset guard."
                    ),
                    breathingCue = "Sharp 'shh' exhale on every punch.",
                    commonMistakes = "Dropping the non-punching hand or hyperextending elbows.",
                    videoQuery = "shadow boxing jab cross proper form darebee"
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
                    kneeSafetyNote = "Zero knee stress.",
                    motionType = ExerciseMotionType.BENCH_PRESS,
                    steps = listOf(
                        "Lie back on bench or mat holding dumbbells over chest.",
                        "Lower under 2-sec control until elbows reach chest height (or lightly touch mat).",
                        "Press smoothly up and squeeze chest."
                    ),
                    breathingCue = "Inhale lowering; exhale pressing.",
                    commonMistakes = "Elbows flaring straight out to 90°.",
                    videoQuery = "dumbbell bench press proper form"
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
                    kneeSafetyNote = "Support with chest on incline bench to keep knees unloaded.",
                    motionType = ExerciseMotionType.BENT_ROW,
                    steps = listOf(
                        "Lie chest-down on 30° incline bench (or brace one hand on bench).",
                        "Pull dumbbells toward hips, squeeze shoulder blades 1 sec, lower smoothly."
                    ),
                    breathingCue = "Exhale rowing up; inhale lowering.",
                    commonMistakes = "Rounding upper back or jerking weight.",
                    videoQuery = "dumbbell row proper form"
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
                    kneeSafetyNote = "Seated.",
                    motionType = ExerciseMotionType.SHOULDER_PRESS,
                    steps = listOf(
                        "Sit upright on bench with dumbbells held in front of chest, palms facing you.",
                        "As you press overhead, rotate wrists smoothly so palms face forward at the top.",
                        "Reverse the rotation cleanly on the way back down."
                    ),
                    breathingCue = "Exhale pressing up; inhale lowering.",
                    commonMistakes = "Arching lower back or rushing the rotation.",
                    videoQuery = "seated arnold press proper form"
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
