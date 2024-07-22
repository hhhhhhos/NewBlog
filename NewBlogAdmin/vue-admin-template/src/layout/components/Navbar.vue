<template>
  <div class="navbar">
    <hamburger :is-active="sidebar.opened" class="hamburger-container" @toggleClick="toggleSideBar" />

    <breadcrumb class="breadcrumb-container" />

    <div class="right-menu">
      <el-dropdown class="avatar-container" trigger="click">
        <div class="avatar-wrapper">
          <img  loading="lazy"  :src=return_avata() class="user-avatar" @click.stop="goToProfile">
          <i class="el-icon-caret-bottom" />
        </div>
        <el-dropdown-menu slot="dropdown" class="user-dropdown">
          <router-link to="/">
            <el-dropdown-item>
              控制面板
            </el-dropdown-item>
          </router-link>
          <a target="_blank" :href=url>
            <el-dropdown-item>商城首页</el-dropdown-item>
          </a>
          <div @click="searchsession">
            <el-dropdown-item>session</el-dropdown-item>
          </div>

          <el-dropdown-item divided @click.native="logout">
            <span style="display:block;">退出登录</span>
          </el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import Breadcrumb from '@/components/Breadcrumb'
import Hamburger from '@/components/Hamburger'
import axios from '@/utils/axios';

export default {
    components: {
        Breadcrumb,
        Hamburger
    },
    data() {
        return {
            url:process.env.VUE_APP_SHOP_URL
        }
    },
    computed: {
        ...mapGetters([
            'sidebar',
            'avatar'
        ])
    },
    methods: {
        searchsession(){
            axios.get('/user/session')
                .then(response=>{
                    console.log(response.data)
                    this.$message({
                        dangerouslyUseHTMLString: true, // html格式
                        showClose:true, // 显示关闭按钮
                        duration:0, // 不会自动关闭
                        message: response.data.join('<br>')
                    });
                }).catch(error=>{
                    this.$message.error("error")
                    console.log(error)
                })
        },
        toggleSideBar() {
            this.$store.dispatch('app/toggleSideBar')
        },
        return_avata(){
            return `${process.env.VUE_APP_STATIC_PATH}avata.webp`
        },
        goToProfile(){
            //this.$message("profile")
            this.$router.push('/user/info')
        },
        async logout() {
            await this.$store.dispatch('user/logout')
            this.$router.push(`/login?redirect=${this.$route.fullPath}`)
        }
    }
}
</script>

<style lang="scss" scoped>
.navbar {
  height: 50px;
  overflow: hidden;
  position: relative;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0,21,41,.08);

  .hamburger-container {
    line-height: 46px;
    height: 100%;
    float: left;
    cursor: pointer;
    transition: background .3s;
    -webkit-tap-highlight-color:transparent;

    &:hover {
      background: rgba(0, 0, 0, .025)
    }
  }

  .breadcrumb-container {
    float: left;
  }

  .right-menu {
    float: right;
    height: 100%;
    line-height: 50px;

    &:focus {
      outline: none;
    }

    .right-menu-item {
      display: inline-block;
      padding: 0 8px;
      height: 100%;
      font-size: 18px;
      color: #5a5e66;
      vertical-align: text-bottom;

      &.hover-effect {
        cursor: pointer;
        transition: background .3s;

        &:hover {
          background: rgba(0, 0, 0, .025)
        }
      }
    }

    .avatar-container {
      margin-right: 30px;

      .avatar-wrapper {
        margin-top: 5px;
        position: relative;

        .user-avatar {
          cursor: pointer;
          width: 40px;
          height: 40px;
          border-radius: 10px;
        }

        .el-icon-caret-bottom {
          cursor: pointer;
          position: absolute;
          right: -20px;
          top: 25px;
          font-size: 12px;
        }
      }
    }
  }
}
</style>
