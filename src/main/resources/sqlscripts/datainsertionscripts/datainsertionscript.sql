-- Insert roles
INSERT INTO roles (id, role_name) VALUES
                                      (1, 'Admin'),
                                      (2, 'Moderator'),
                                      (3, 'User');

-- Insert tags
INSERT INTO tags (tag_name) VALUES
                                ('engine'),
                                ('suspension'),
                                ('tires'),
                                ('brakes'),
                                ('transmission'),
                                ('exhaust'),
                                ('interior'),
                                ('electronics'),
                                ('performance'),
                                ('maintenance');

-- Insert users
INSERT INTO users (id, first_name, last_name, email, username, password, role_id, is_blocked, profile_url) VALUES
                                                                                                               (1, 'Alice', 'Smith', 'alice.smith@example.com', 'alice_smith', 'password123', 1, 0, ''),
                                                                                                               (2, 'Bob', 'Johnson', 'bob.johnson@example.com', 'bob_johnson', 'password123', 1, 0, ''),
                                                                                                               (3, 'Charlie', 'Williams', 'charlie.williams@example.com', 'charlie_williams', 'password123', 2, 0, ''),
                                                                                                               (4, 'David', 'Jones', 'david.jones@example.com', 'david_jones', 'password123', 2, 0, ''),
                                                                                                               (5, 'Eva', 'Brown', 'eva.brown@example.com', 'eva_brown', 'password123', 3, 0, ''),
                                                                                                               (6, 'Frank', 'Davis', 'frank.davis@example.com', 'frank_davis', 'password123', 3, 0, ''),
                                                                                                               (7, 'Grace', 'Miller', 'grace.miller@example.com', 'grace_miller', 'password123', 3, 0, ''),
                                                                                                               (8, 'Hank', 'Wilson', 'hank.wilson@example.com', 'hank_wilson', 'password123', 3, 0, ''),
                                                                                                               (9, 'Ivy', 'Moore', 'ivy.moore@example.com', 'ivy_moore', 'password123', 3, 0, ''),
                                                                                                               (10, 'Jack', 'Taylor', 'jack.taylor@example.com', 'jack_taylor', 'password123', 3, 0, '');

-- Insert phone numbers (only for admins)
INSERT INTO user_phone_numbers (user_id, phone_number) VALUES
                                                           (1, '+1-555-1234'),
                                                           (2, '+1-555-5678');

-- Insert posts
INSERT INTO posts (user_id, title, content, likes, created_at) VALUES
                                                                   (5, 'How to Improve Engine Performance', 'Here are some tips and tricks to get the most out of your engine...', 1, '2024-08-17 10:00:00'),
                                                                   (6, 'Best Tires for Winter Driving', 'Winter driving requires special tires. Check out these recommendations...', 1, '2024-08-17 11:00:00'),
                                                                   (7, 'Upgrading Your Suspension', 'If you’re looking to upgrade your suspension, here’s what you need to know...', 1, '2024-08-17 12:00:00'),
                                                                   (8, 'Maintaining Your Brakes', 'Regular brake maintenance is crucial for safety. Here are some tips...', 1, '2024-08-17 13:00:00'),
                                                                   (9, 'Understanding Transmission Issues', 'Transmission problems can be costly. Learn how to identify and fix them...', 1, '2024-08-17 14:00:00'),
                                                                   (10, 'Exhaust Systems Explained', 'A good exhaust system can enhance your car’s performance. Here’s a guide...', 1, '2024-08-17 15:00:00'),
                                                                   (5, 'Interior Mods for Better Comfort', 'Improve your car’s interior with these modifications...', 1, '2024-08-17 16:00:00'),
                                                                   (6, 'The Latest in Automotive Electronics', 'Keep up with the latest tech for your car...', 1, '2024-08-17 17:00:00'),
                                                                   (7, 'Performance Upgrades on a Budget', 'Want to upgrade performance without breaking the bank? Here’s how...', 1, '2024-08-17 18:00:00'),
                                                                   (8, 'Routine Maintenance Tips', 'Regular maintenance can keep your car running smoothly. Check out these tips...', 1, '2024-08-17 19:00:00');

-- Insert comments
INSERT INTO comments (post_id, user_id, content, created_at) VALUES
                                                                 (1, 6, 'Great tips! I’ll try some of these suggestions.', '2024-08-17 10:30:00'),
                                                                 (2, 7, 'I’ve had great success with the tires you mentioned.', '2024-08-17 11:30:00'),
                                                                 (3, 8, 'I need to upgrade my suspension. Thanks for the advice!', '2024-08-17 12:30:00'),
                                                                 (4, 9, 'Brake maintenance is so important. Appreciate the tips!', '2024-08-17 13:30:00'),
                                                                 (5, 10, 'Good overview of transmission issues. Very helpful.', '2024-08-17 14:30:00'),
                                                                 (6, 5, 'Love the interior mods you suggested!', '2024-08-17 15:30:00'),
                                                                 (7, 6, 'The electronics upgrades sound exciting.', '2024-08-17 16:30:00'),
                                                                 (8, 7, 'Awesome budget performance tips.', '2024-08-17 17:30:00'),
                                                                 (9, 8, 'Routine maintenance is key. Thanks for the reminders!', '2024-08-17 18:30:00'),
                                                                 (10, 9, 'This is a great guide for exhaust systems.', '2024-08-17 19:30:00');

-- Insert likes
INSERT INTO likes (user_id, post_id) VALUES
                                         (5, 1),
                                         (6, 2),
                                         (7, 3),
                                         (8, 4),
                                         (9, 5),
                                         (10, 6),
                                         (5, 7),
                                         (6, 8),
                                         (7, 9),
                                         (8, 10);

-- Insert post_tags
INSERT INTO post_tags (post_id, tag_id) VALUES
                                            (1, 9),  -- performance
                                            (2, 3),  -- tires
                                            (3, 2),  -- suspension
                                            (4, 4),  -- brakes
                                            (5, 5),  -- transmission
                                            (6, 6),  -- exhaust
                                            (7, 7),  -- interior
                                            (8, 8),  -- electronics
                                            (9, 9),  -- performance
                                            (10, 10); -- maintenance
