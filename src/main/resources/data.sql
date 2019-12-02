MERGE INTO USERS (
    user_id,
    user_name,
    user_login,
    user_password,
    status,
    user_role
) KEY (user_id)
VALUES
  (
    '4d6b8d54-3d13-40ed-978b-8e71a6abc58b',
    'ADMIN',
    'admin',
    '$2a$10$lP9EJ9JrU8pDNUTi9GijReKQnF6svn5Fs0DLIjMU0luwRk4zWK4UC',
    'ACTIVE',
    'ADMIN'
  );