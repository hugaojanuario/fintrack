CREATE TABLE monthly_budgets (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    reference_month DATE NOT NULL,
    income NUMERIC(19, 2) NOT NULL,
    total_expenses NUMERIC(19, 2) NOT NULL,
    total_debt_installments NUMERIC(19, 2) NOT NULL,
    balance NUMERIC(19, 2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT fk_monthly_budget_user FOREIGN KEY (user_id) REFERENCES users(id)
);
