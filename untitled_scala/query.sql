-- Создание таблицы и вставка данных
CREATE TABLE oper (
    client varchar,
    transaction_date date,
    type varchar,
    amount int
);

INSERT INTO oper VALUES
('a', '2023-06-02', 'in', 1985),
('a', '2023-06-03', 'in', 1577),
('b', '2023-06-04', 'out', 1557),
('b', '2023-06-05', 'in', 1468),
('c', '2023-07-06', 'in', 582),
('c', '2023-07-08', 'out', 550),
('d', '2023-06-09', 'in', 1556);

-- Запрос для поиска клиентов с балансом больше 500 рублей на 2023-07-07
WITH client_balances AS (
    SELECT 
        client,
        SUM(
            CASE 
                WHEN type = 'in' THEN amount 
                WHEN type = 'out' THEN -amount 
                ELSE 0 
            END
        ) as balance
    FROM oper
    WHERE transaction_date <= '2023-07-07'
    GROUP BY client
)
SELECT 
    client,
    balance
FROM client_balances
WHERE balance > 500
ORDER BY balance DESC;
