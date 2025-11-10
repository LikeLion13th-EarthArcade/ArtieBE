local owner = ARGV[1]
local ttl = tonumber(ARGV[2])

-- Lua는 인덱스가 1부터 시작, 인덱스 1부터 KEYS의 #(COUNT)만큼 반복문으로 돌린다
for i = 1, #KEYS do
    -- 현재 인덱스가 가리키는 KEYS의 VALUE를 가져온다
    local current = redis.call('GET', KEYS[i])
    -- 만약 VALUE가 NULL이 아니고, 현재 락을 걸고자 하는 유저가 아닌 경우 -> 이미 다른 유저가 잡은 락
    if current and current ~= owner then
        -- 실패한 키의 값을 리턴
        return {0, KEYS[i]}
    end
end
-- 인덱스 1부터 KEYS의 #(COUNT)만큼 반복문
for i = 1, #KEYS do
    -- 각각의 VALUE를 owner 값을 넣고 ms 단위의 ttl 설정
    redis.call('SET', KEYS[i], owner, 'PX', ttl)
end
-- 모든 락 설정이 끝남
return {1}