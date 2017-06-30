import {Component, OnInit} from '@angular/core';
import {DevicesService} from './api/devices.service';
import {Device} from './model/device';

@Component({
    selector: 'app-root',
    template: `
        <nav>
            <a href="#">End points</a>
            <a href="#">Devices</a>
        </nav>
        <div>
            <ul>
                <li *ngFor="let device of devices">
                    <button (click)="changeDeviceState(device)">{{device.isStarted() ? "Stop" : "Start"}}</button>
                    {{device.name}} : {{device.state}}
                </li>
            </ul>
        </div>
    `,
    styles: [],
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
