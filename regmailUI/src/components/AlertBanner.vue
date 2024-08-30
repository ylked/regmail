<script setup lang="ts">

import {useAlertStore} from "@/types/store/Alerts";

const alerts = useAlertStore();

</script>

<template>
  <v-alert
    v-for="(alert, index) in alerts.alerts"
    :key="index"
    :value="true"
    :type="alert.type"
    :title="alert.title"
    :text="alert.message"
    closable
  >
  </v-alert>

</template>

<style scoped>

</style>
