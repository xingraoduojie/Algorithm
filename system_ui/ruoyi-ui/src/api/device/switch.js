import request from '@/utils/sdnrequest'

export function listSwitch(query) {
  return request({
    url: '/stats/switches',
    method: 'get',
    params: query
  })
}

export function getSwitchDesc(dpid) {
  return request({
    url: '/stats/desc/' + dpid,
    method: 'get',
  })
}
