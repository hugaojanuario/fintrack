CREATE TABLE recommendations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    investor_profile VARCHAR(50) NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    asset_type VARCHAR(50) NOT NULL,
    expected_return NUMERIC(8, 4),
    risk_level VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT fk_recommendation_user FOREIGN KEY (user_id) REFERENCES users(id)
);
