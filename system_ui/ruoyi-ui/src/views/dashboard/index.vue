<template>
    <div class="app-container">
      <el-row :gutter="20">
        <el-col :span="12">
          <el-card class="box-card">
            <div slot="header" class="clearfix">
              <span>下发流表</span>
            </div>
            <el-form :model="ruleForm" :rules="rules" ref="ruleForm" label-width="100px" class="demo-ruleForm">
              <el-form-item label="交换机" prop="region">
                <el-select v-model="ruleForm.switch" placeholder="交换机1">
                  <el-option label="交换机1" value="1"></el-option>
                  <el-option label="交换机2" value="2"></el-option>
                  <el-option label="交换机3" value="3"></el-option>
                </el-select>
              </el-form-item>
              <el-form-item label="COOKIE" required>
                <el-input-number v-model="ruleForm.cookie" @change="handleChange" :min="1" :max="10" label="描述文字"></el-input-number>
              </el-form-item>
              <el-form-item label="优先级" required>
                <el-input-number v-model="ruleForm.priority" @change="handleChange" :min="1" :max="10" label="描述文字"></el-input-number>
              </el-form-item>
              <el-form-item label="match" prop="desc">
                <el-input type="textarea" v-model="ruleForm.match"></el-input>
              </el-form-item>
              <el-form-item label="actions" prop="desc">
                <el-input type="textarea" v-model="ruleForm.actions"></el-input>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="submitForm('ruleForm')">下发流表</el-button>
                <el-button @click="resetForm('ruleForm')">清除</el-button>
              </el-form-item>
            </el-form>
          </el-card>
        </el-col>
        <el-col :span="12">
          <div height="100px"></div>
          <img src="@/assets/topo.jpg" width="100%" height="430px" alt="拓扑图">
        </el-col>
      </el-row>
  
      <h1>流表信息</h1>
      <el-table v-loading="loading" :data="flowList">
        <el-table-column label="datapath id" prop="dpid" align="center" />
        <el-table-column label="信息长度" prop="length" align="center" />
        <el-table-column label="流表id" prop="table_id" align="center" />
        <el-table-column label="流存活时间（秒）" prop="duration_sec" align="center" />
        <el-table-column label="流存活时间（纳秒）" prop="duration_nsec" align="center" />
        <el-table-column label="优先级" prop="priority" align="center" />
        <el-table-column label="idle_timeout" prop="idle_timeout" align="center" />
        <el-table-column label="剩余秒数" prop="hard_timeout" align="center" />
        <el-table-column label="标志位 " prop="flags" align="center" />
        <el-table-column label="cookie" prop="cookie" align="center" />
        <el-table-column label="流中包数量" prop="packet_count" align="center" />
        <el-table-column label="流字节数" prop="byte_count" align="center" />
        <el-table-column label="match" prop="match" align="center" />
        <el-table-column label="actions" prop="actions" align="center" />
      </el-table>
    </div>
  </template>
  
  <style>
  .el-row {
    margin-bottom: 20px;
    &:last-child {
      margin-bottom: 0;
    }
  }
  .el-col {
    border-radius: 4px;
  }
  .bg-purple-dark {
    background: #99a9bf;
  }
  .bg-purple {
    background: #d3dce6;
  }
  .bg-purple-light {
    background: #e5e9f2;
  }
  .grid-content {
    border-radius: 4px;
    min-height: 36px;
  }
  .row-bg {
    padding: 10px 0;
    background-color: #f9fafc;
  }
  </style>
  
  <script>
  export default {
    name: 'SdnTopo',
    data() {
      return {
        num: 1,
        ruleForm: {
          name: '',
          switch: '',
          date1: '',
          date2: '',
          delivery: false,
          type: [],
          resource: '',
          desc: '',
          actions: '',
          cookie: 1,
          match: '',
          priority: 1,
        },
        rules: {
          name: [
            { required: true, message: '请选择交换机', trigger: 'blur' },
          ],
          cookie: [
            { required: true, message: '请设置cookie', trigger: 'change' }
          ],
          priority: [
            { required: true, message: '请设置优先级', trigger: 'change' }
          ]
        },
        loading: false,
        flowList: [
          {
            dpid: '1',
            length: '100',
            table_id: '0',
            duration_sec: '1000',
            duration_nsec: '1000000',
            priority: '1',
            idle_timeout: '30',
            hard_timeout: '60',
            flags: '1',
            cookie: '1',
            packet_count: '1000',
            byte_count: '1000000',
            match: 'eth_type=0x0800,ipv4_src=192.168.1.1',
            actions: 'output=2'
          },
          {
            dpid: '2',
            length: '100',
            table_id: '0',
            duration_sec: '2000',
            duration_nsec: '2000000',
            priority: '2',
            idle_timeout: '30',
            hard_timeout: '60',
            flags: '1',
            cookie: '2',
            packet_count: '2000',
            byte_count: '2000000',
            match: 'eth_type=0x0800,ipv4_src=192.168.1.2',
            actions: 'output=3'
          }
        ],
        dpidList: [],
      };
    },
    methods: {
      submitForm(formName) {
        this.$refs[formName].validate((valid) => {
          if (valid) {
            alert('submit!');
          } else {
            console.log('error submit!!');
            return false;
          }
        });
      },
      resetForm(formName) {
        this.$refs[formName].resetFields();
      },
      handleChange(value) {
        console.log(value);
      }
    }
  }
  </script>

<style>
.el-button--primary {
  color: #FFFFFF;
  background-color: rgb(118 112 112);
  border-color: rgb(118 112 112);
}
</style>
