import {Injectable} from '@angular/core';
import {Device} from '../model/device';

const DEVICES: Device[] = [
    new Device("LT31", Device.STARTED),
    new Device("CL31", Device.STOPPED),
    new Device("CT25K", Device.STOPPED)
];

@Injectable()
export class DevicesService {

    getDevices(): Promise<Device[]> {
        return Promise.resolve(DEVICES);
    }

    loadDevice(deviceName: string): Promise<Device> {
        let device = this.findDevice(deviceName);
        if (device === undefined) {
            device = new Device(deviceName, Device.STOPPED);
            DEVICES.push(device);
        }

        return Promise.resolve(device);
    }

    startDevice(device: Device): Promise<Device> {
        device.state = Device.STARTED;
        return Promise.resolve(device);
    }

    stopDevice(device: Device): Promise<Device> {
        device.state = Device.STOPPED;
        return Promise.resolve(device);
    }

    switchDevice(device: Device): Promise<Device> {
        return device.isStarted()
            ? this.stopDevice(device)
            : this.startDevice(device);
    }

    private findDevice(deviceName: string): Device | undefined {
        return DEVICES.find(d => d.name == deviceName);
    }
}