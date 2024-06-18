<template>
  <div :class="{'has-logo':showLogo}">
    <logo v-if="showLogo" :collapse="isCollapse" />
    <el-scrollbar wrap-class="scrollbar-wrapper" id="myid">
      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapse"
        :background-color="variables.menuBg"
        :text-color="variables.menuText"
        :unique-opened="false"
        :active-text-color="variables.menuActiveText"
        :collapse-transition="false"
        mode="vertical"
      >
        <el-menu-item style="height: 70px;padding: 10px 0 0 15px;">
          <div v-show="!isCollapse">
            <h2 style="display: inline;">西巴商城管理系统</h2>
          </div>
        </el-menu-item>
        <sidebar-item v-for="route in routes" :key="route.path" :item="route" :base-path="route.path" />
      </el-menu>
    </el-scrollbar>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import Logo from './Logo'
import SidebarItem from './SidebarItem'
import variables from '@/styles/variables.scss'

export default {
  components: { SidebarItem, Logo },
  computed: {
    ...mapGetters([
      'sidebar'
    ]),
    routes() {
      return this.$router.options.routes
    },
    activeMenu() {
      const route = this.$route
      const { meta, path } = route
      // if set path, the sidebar will highlight the path you set
      if (meta.activeMenu) {
        return meta.activeMenu
      }
      return path
    },
    showLogo() {
      return this.$store.state.settings.sidebarLogo
    },
    variables() {
      return variables
    },
    isCollapse() {
      return !this.sidebar.opened
    }
  },
  
  mounted(){
    
    // 观察id为myid的元素的宽度

    const element = document.getElementById('myid');

    // 监听元素宽度变化
    const resizeObserver = new ResizeObserver(entries => {
      for (let entry of entries) {
        // 判为手机 它会缩 所以归零
        if(!this.$store.state.app.is_mobile)
            this.$store.state.app.scrollbar_width = entry.target.clientWidth
        else
            this.$store.state.app.scrollbar_width = 0

        console.log("myid.clientWidth:" + this.$store.state.app.scrollbar_width);
      }
    });

    resizeObserver.observe(element);

  },
  beforeDestroy() {
    if (this.resizeObserver) {
      this.resizeObserver.disconnect();  // 停止观察
    }
  }
  

}
</script>
