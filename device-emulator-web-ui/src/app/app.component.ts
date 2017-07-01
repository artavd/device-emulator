import {Component, OnInit} from '@angular/core';
import {DevicesService} from './api/devices.service';
import {Device} from './model/device';

@Component({
    selector: 'app-root',
    templateUrl: "view/app.html",
    providers: [DevicesService]
})
export class AppComponent implements OnInit {

    private devices: Device[];

    constructor(private devicesService: DevicesService) {
    }

    ngOnInit(): void {
        this.devicesService.getDevices().then(d => this.devices = d);
    }

    changeDeviceState(device: Device): void {
        this.devicesService.switchDevice(device);
    }
}
