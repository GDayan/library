CREATE TABLE expenses (
                          id          BIGSERIAL PRIMARY KEY,
                          description VARCHAR(255)   NOT NULL,
                          category    VARCHAR(255)   NOT NULL,
                          amount      DOUBLE PRECISION NOT NULL,
                          created_at  TIMESTAMP      NOT NULL,
                          user_id     BIGINT         NOT NULL,

                          CONSTRAINT fk_expenses_user
                              FOREIGN KEY (user_id) REFERENCES users(id)
                                  ON DELETE CASCADE
);

CREATE INDEX idx_expenses_user_id ON expenses(user_id);
CREATE INDEX idx_expenses_user_created_at ON expenses(user_id, created_at);