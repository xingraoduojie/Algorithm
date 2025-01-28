import request from '@/utils/sdnrequest'

export function getPortStats(dpid) {
  return request({
    url: '/stats/port/' + dpid,
    method: 'get',
  })
}

export function getPortDesc(dpid) {
  return request({
    url: '/stats/portdesc/' + dpid,
    method: 'get',
  })
}
