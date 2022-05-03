local workerHashKey = KEYS[1]
local clientId = ARGV[1]
local lockTime = ARGV[2]
local from = ARGV[3]
local to = ARGV[4]
local nowSecondsStr = redis.call('time')[1]
local idleWorkerId = nil
for workerId = from, to, 1 do
    local workerInfo = redis.call('hget', workerHashKey, workerId)
    if workerInfo == false then
        idleWorkerId = workerId
        break
    end
    local workerExpiredTime = tonumber(string.sub(workerInfo, string.len(clientId) + 1, -1))
    if tonumber(nowSecondsStr) > workerExpiredTime then
        idleWorkerId = workerId
        break
    end
end
if idleWorkerId == nil then
    return nil
else
    local expiredTime = tonumber(nowSecondsStr) + lockTime
    redis.call('hset', workerHashKey, idleWorkerId, clientId .. tostring(expiredTime))
    return idleWorkerId
end