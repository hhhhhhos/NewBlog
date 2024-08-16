<!-- eslint-disable vue/multi-word-component-names -->
<template>
  <div>
    <vue-page-transition name="slide-left">
        <router-view/>
    </vue-page-transition>

  </div>
</template>

<script>

export default {
    components: {
    },
    data() {
        return{
        }
    },
    methods:{
    }
}
</script>

