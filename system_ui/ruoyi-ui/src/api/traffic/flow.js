import request from '@/utils/sdnrequest'

export function getFlowDesc(dpid) {
  return request({
    url: '/stats/flow/' + dpid,
    method: 'get',
  })
}

export function getAggregateFlow(dpid) {
  return request({
    url: '/stats/aggregateflow/' + dpid,
    method: 'get',
  })
}
