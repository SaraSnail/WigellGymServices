INSERT INTO gym_instructor(gym_instructor_name,gym_instructor_specialty,gym_instructor_is_active) VALUES
('Erik Quill','DANCE',true),
('Mia Sundsvall','STRENGTH',true),
('Will Stenhagen','YOGA',false);

INSERT INTO gym_workout(gym_workout_name,gym_workout_traning_type,gym_workout_max_participants,gym_workout_price,gym_instructor_gym_instructor_id,gym_workout_date_time, gym_workout_is_active) VALUES
    ('Tyngdlyft - 1','STRENGTH',8,220.0,2,'2025-09-20 11:15:00.00',true),
('Bugg - 1','DANCE',20,190.98,1,'2025-10-22 15:00:00.00',true),
('Tyngdlyft - 2','STRENGTH',8,220.0,2,'2025-10-28 11:15:00.00',true),
('Tyngdlyft - Solo','STRENGTH',1,440.0,2,'2025-10-28 15:15:00.00',true);

INSERT INTO gym_customer(gym_customer_name,gym_customer_is_active) VALUES
('sara',true),
('amanda',true),
('alex',true);

INSERT INTO gym_booking(gym_customer_gym_customer_id,gym_workout_gym_workout_id,gym_booking_date,gym_booking_price,gym_booking_is_active) VALUES
(1,1,'2025-09-15 11:32:54.22',260.0,true),
(1,2,'2025-10-02 14:12:57.23',230.98,true),
(2,3,'2025-10-17 13:08:12.02',260.0,false),
(3,3,'2025-08-17 10:38:19.41',260.0,true),
(1,3,'2025-10-12 17:43:24.67',260.0,false),
(1,4,'2025-10-20 11:27:42.15',480.0,true);


