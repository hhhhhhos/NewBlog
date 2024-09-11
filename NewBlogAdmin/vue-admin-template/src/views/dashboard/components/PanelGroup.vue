<template>
  <el-row :gutter="40" class="panel-group">
  
    <el-col v-for="(item,key) in dashboard" :key="key"  
    :xs="12" :sm="8" :lg="4" class="card-panel-col">
      <div class="card-panel">
        <div class="card-panel-description">
          <div class="card-panel-text">
            {{dashboard_name[key]}} <div v-show="typeof item === 'number' && item>dashboard_params_yesterday[key]" class="triangle"></div>
          </div>
          <div class="card-panel-num">
            {{item}}
        </div>
        </div>
      </div>
    </el-col>

  </el-row>
</template>

<script>
//import CountTo from 'vue-count-to'
import axios from '@/utils/axios';

export default {
    components: {
        //CountTo
    },
    data(){
        return{
            dashboard:{
                role: null,            // 对应后端的 "role"
                name: null,            // 对应后端的 "name"
                home_visit_num: null,  // 对应后端的 "home_visit_num"
                dashboard_visit_num:null,
                request_num: null,     // 对应后端的 "request_num"

                product_num: null,     // 对应后端的 "product_num"
                user_num: null,        // 对应后端的 "user_num"
                comment_num: null,     // 对应后端的 "comment_num"
                chat_num: null,        // 对应后端的 "chat_num"
                kefu_num: null         // 对应后端的 "kefu_num"
            },
            dashboard_name:{
                name: '姓名',            // 对应后端的 "name"
                role: '角色',            // 对应后端的 "role"
                home_visit_num: '首页访客',  // 对应后端的 "home_visit_num"
                dashboard_visit_num:'后台访客',
                request_num: '总请求数',     // 对应后端的 "request_num"

                product_num: '文章数',     // 对应后端的 "product_num"
                user_num: '用户数',        // 对应后端的 "user_num"
                comment_num: '评论数',     // 对应后端的 "comment_num"
                chat_num: '聊天条数',        // 对应后端的 "chat_num"
                kefu_num: 'GPT对话'         // 对应后端的 "kefu_num"
            },
            dashboard_params_yesterday:{

            }
        }
    },
    methods: {
        handleSetLineChartData(type) {
            this.$emit('handleSetLineChartData', type)
        },
        return_color(){
            const l = ['shopping','money','message','people']
            return l[Math.floor(Math.random() * l.length)]
        },

    },
    mounted(){
        axios.get('/user-agent-details/init_dashboard')
            .then(response=>{
                console.log(response.data)
                for (let [key, value] of Object.entries(response.data)) {
                    if(key!=='dashboard_params_yesterday')this.dashboard[key]=value;
                    else this.dashboard_params_yesterday = value;
                }
            }).catch(error=>{
                console.log(error)
            })
    }
}
</script>

<style lang="scss" scoped>
.triangle {
    margin: 4px 0 0 8px;
  width: 0;
  height: 0;
  border-left: 6px solid transparent;
  border-right: 6px solid transparent;
  border-bottom: 10px solid green;
}


.panel-group {
  margin-top: 18px;

  .card-panel-col {
    margin-bottom: 32px;
  }

  .card-panel {
    cursor: pointer;
    font-size: 12px;
    position: relative;
    overflow: hidden;
    color: #666;
    background: #fff;
    box-shadow: 4px 4px 40px rgba(0, 0, 0, .05);
    border-radius: 20px;
    border-color: rgba(0, 0, 0, .05);

    &:hover {
        background: #f3f3f3;

      .card-panel-icon-wrapper {
        color: #fff;
      }

      .icon-people {
        background: #40c9c6;
      }

      .icon-message {
        background: #36a3f7;
      }

      .icon-money {
        background: #f4516c;
      }

      .icon-shopping {
        background: #34bfa3
      }
    }

    .icon-people {
      color: #40c9c6;
    }

    .icon-message {
      color: #36a3f7;
    }

    .icon-money {
      color: #f4516c;
    }

    .icon-shopping {
      color: #34bfa3
    }


    .card-panel-icon-wrapper {
      float: left;
      margin: 14px 0 0 14px;
      padding: 16px;
      transition: all 0.38s ease-out;
      border-radius: 6px;
    }

    .card-panel-icon {
      float: left;
      font-size: 48px;

    }

    .card-panel-description {
      font-weight: bold;
      margin: 26px;

      .card-panel-text {
        line-height: 18px;
        color: rgba(0, 0, 0, 0.45);
        font-size: 16px;
        margin-bottom: 12px;
        text-align: left;
        display: flex;
      }

      .card-panel-num {
        font-size: 20px;
      }
    }
  }
}
</style>
