import {defineStore} from "pinia";

export enum AlertType {
  OK = 'success',
  INFO = 'info',
  WARN = 'warning',
  ERROR = 'error',
}

export interface Alert {
  id: number
  type: AlertType
  title: string
  message: string
}

export const useAlertStore = defineStore('alerts', {
  state: () => ({
    alerts: [] as Alert[],
    currentId: 0,
  }),
  actions: {
    add(type: AlertType, title: string, message: string, timeout?: number) {
      this.alerts.push({
          id: this.currentId++,
          type,
          title,
          message,
        }
      )
      if (timeout) {
        setTimeout(() => {
          this.remove(this.currentId - 1);
        }, timeout);
      }
    },
    remove(id: number) {
      const index = this.alerts.findIndex((alert) => alert.id === id);
      if (index !== -1) {
        this.alerts.splice(index, 1);
      }
    },
    info(title: string, message: string, timeout?: number) {
      this.add(AlertType.INFO, title, message, timeout);
    },
    warn(title: string, message: string, timeout?: number) {
      this.add(AlertType.WARN, title, message, timeout);
    },
    error(title: string, message: string, timeout?: number) {
      this.add(AlertType.ERROR, title, message, timeout);
    },
    ok(title: string, message: string, timeout?: number) {
      this.add(AlertType.OK, title, message, timeout);
    },
  }
})
