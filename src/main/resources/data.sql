MERGE INTO USERS (
    user_id,
    user_name,
    user_login,
    user_password,
    status,
    user_role
) KEY (user_id)
VALUES
  ('4d6b8d54-3d13-40ed-978b-8e71a6abc58b', 'ADMIN', 'admin', 'set password', 'ACTIVE', 'ADMIN');