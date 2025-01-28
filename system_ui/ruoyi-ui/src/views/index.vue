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
        <iframe
          src="http://101.42.150.135:8080/"
          width="100%" height="450px;"></iframe>
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

import {listSwitch} from "@/api/device/switch";
import {getFlowDesc} from "@/api/traffic/flow";

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
      loading: true,
      flowList: [],
      dpidList: [],
    };
  },
  created() {
    this.getDpidList()
  },
  methods: {
    getDpidList() {
      this.loading = true
      listSwitch('').then(response => {
        this.dpidList = response
        this.getFlowDesc()
      })
    },
    getFlowDesc() {
      for (let i in this.dpidList) {
        let dpid = this.dpidList[i]
        getFlowDesc(dpid).then(response => {
          let desc = response[dpid]
          desc = desc['0']
          desc['dpid'] = dpid
          this.flowList.push(desc)
          console.log(desc['match'])
          this.loading = false
        })
      }
    },
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
