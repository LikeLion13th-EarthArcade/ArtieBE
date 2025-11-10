local owner = ARGV[1]
local released = 0

-- KEYS의 길이만큼 반복
for i = 1, #KEYS do
    -- KEY의 VALUE가 소유자라면
    if redis.call('GET', KEYS[i]) == owner then
        -- 삭제
        redis.call('DEL', KEYS[i])
        release = released + 1
    end
end

return released