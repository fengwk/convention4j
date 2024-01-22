local workerHashKey = KEYS[1]
local clientId = ARGV[1]
local lockTime = ARGV[2]
local workerId = ARGV[3]
local workerInfo = redis.call('hget', workerHashKey, workerId)
if not workerInfo or type(workerInfo) ~= 'string' then
    return nil
end
local workerClientId = string.sub(workerInfo, 1, string.len(clientId))
if workerClientId == clientId then
    local nowSecondsStr = redis.call('time')[1]
    local expiredTime = tonumber(nowSecondsStr) + lockTime
    redis.call('hset', workerHashKey, workerId, clientId .. tostring(expiredTime));
    return expiredTime
else
    return nil
end