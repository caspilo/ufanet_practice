# CREATE TRIGGER update_status_before_update BEFORE UPDATE ON tasks
#     FOR EACH ROW BEGIN
#     IF @execution_time <= NOW() AND @status <> 'READY' THEN
#         SET @status = 'READY';
#     IF @execution_time > NOW() AND @status <> 'PENDING' THEN
#         SET @status = 'PENDING';
#     end if;
#     end if;
# end;
#
# CREATE TRIGGER update_status_before_insert BEFORE INSERT ON tasks
#     FOR EACH ROW BEGIN
#     IF @execution_time <= NOW() AND @status <> 'READY' THEN
#         SET @status = 'READY';
#         IF @execution_time > NOW() AND @status <> 'PENDING' THEN
#             SET @status = 'PENDING';
#         end if;
#     end if;
# end;
