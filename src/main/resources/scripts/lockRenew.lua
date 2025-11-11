local owner = ARGV[1]
local ttl = tonumber(ARGV[2])
local renewed = 0

-- KEYS의 길이만큼 반복
for i = 1, #KEYS do
    -- 키가 소유자의 것이라면
    if redis.call('GET', KEYS[i]) == owner then
        -- 만료시간 연장
        redis.call('PEXPIRE', KEYS[i], ttl)
        renewed = renewed + 1
    end
end

return renewed