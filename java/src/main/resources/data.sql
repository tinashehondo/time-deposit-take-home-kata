
INSERT INTO time_deposits (id, plan_type, days, balance) VALUES
                                                             (1, 'basic', 45, 10000.00),
                                                             (2, 'student', 90, 5000.00),
                                                             (3, 'premium', 60, 20000.00),
                                                             (4, 'basic', 25, 3000.00),
                                                             (5, 'student', 400, 8000.00),
                                                             (6, 'premium', 120, 15000.00);


INSERT INTO withdrawals (id, time_deposit_id, amount, date) VALUES
                                                                (1, 1, 500.00, '2024-01-15'),
                                                                (2, 2, 200.00, '2024-01-20'),
                                                                (3, 3, 1000.00, '2024-02-01'),
                                                                (4, 6, 750.00, '2024-01-25');
