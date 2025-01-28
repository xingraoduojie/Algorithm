<template>
    <div class="dashboard-editor-container">
      <panel-group @handleSetLineChartData="handleSetLineChartData" />
  
      <!-- ip表 -->
      <el-row :gutter="32">
        <el-col :xs="24" :sm="24" :lg="24">
          <div class="chart-wrapper">
            <el-table :data="list" style="width: 100%; padding-top: 15px;">
              <el-table-column label="序号" width="50">
                <template slot-scope="scope">
                  {{ scope.row.order_no }}
                </template>
              </el-table-column>
              <el-table-column label="时间" min-width="155" align="center">
                <template slot-scope="scope">
                  {{ formatDate(scope.row.timestamp) }}
                </template>
              </el-table-column>
              <el-table-column label="源IP地址" min-width="175" align="center">
                <template slot-scope="scope">
                  {{ scope.row.price }}
                </template>
              </el-table-column>
              <el-table-column label="检测结果" width="100" align="center">
                <template slot-scope="{ row }">
                  <el-tag :type="row.status | statusFilter">
                    {{ row.status }}
                  </el-tag>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-col>
      </el-row>
    </div>
  </template>
  
  <script>
  import PanelGroup from './dashboard/PanelGroup'
  import RaddarChart from './dashboard/RaddarChart'
  import PieChart from './dashboard/PieChart'
  import BarChart from './dashboard/BarChart'
  // import { transactionList } from '@/api/remote-search'
  
  export default {
    name: 'Index',
    components: {
      PanelGroup,
      RaddarChart,
      PieChart,
      BarChart
    },
    data() {
      return {
        list: [
          { order_no: 1, timestamp: 1625078400000, price: '192.168.1.1', status: 'normol' },
          { order_no: 2, timestamp: 1625164800000, price: '192.168.1.2', status: 'DDOS' },
          { order_no: 3, timestamp: 1625251200000, price: '192.168.1.3', status: 'normol' },
          { order_no: 4, timestamp: 1625337600000, price: '192.168.1.4', status: 'DDOS' },
          { order_no: 5, timestamp: 1625424000000, price: '192.168.1.5', status: 'normol' },
          { order_no: 6, timestamp: 1625510400000, price: '192.168.1.6', status: 'DDOS' },
          { order_no: 7, timestamp: 1625596800000, price: '192.168.1.7', status: 'normol' }
        ]
      }
    },
    filters: {
      statusFilter(status) {
        const statusMap = {
          normol: 'success',
          DDOS: 'danger'
        }
        return statusMap[status]
      },
      orderNoFilter(str) {
        return str.substring(0, 30)
      }
    },
    methods: {
      handleSetLineChartData(type) {
        this.lineChartData = lineChartData[type]
      },
      // 数据转为年月日
      formatDate(timestamp) {
        const date = new Date(timestamp)
        const year = date.getFullYear()
        const month = ('0' + (date.getMonth() + 1)).slice(-2)
        const day = ('0' + date.getDate()).slice(-2)
        return `${year}-${month}-${day}`
      }
    }
  }
  </script>
  
  <style lang="scss" scoped>
  .dashboard-editor-container {
    padding: 32px;
    background-color: rgb(240, 242, 245);
    position: relative;
  
    .chart-wrapper {
      background: #fff;
      padding: 16px 16px 0;
      margin-bottom: 32px;
    }
  }
  
  @media (max-width: 1024px) {
    .chart-wrapper {
      padding: 8px;
    }
  }
  </style>