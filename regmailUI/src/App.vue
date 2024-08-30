<template>
  <v-app>
    <v-main>
      <AlertBanner />
      <router-view />
    </v-main>
  </v-app>
</template>

<script lang="ts" setup>
  //
import AlertBanner from "@/components/AlertBanner.vue";
</script>
