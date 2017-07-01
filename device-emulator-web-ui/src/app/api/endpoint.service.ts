import {Injectable} from '@angular/core';
import {EndPoint} from '../model/endpoint';

const END_POINTS: EndPoint[] = [
    new EndPoint("server", "192.168.1.1", 7001, "DISCONNECTED"),
    new EndPoint("localhost", "192.168.1.232", 8888, "DISCONNECTED")
];

@Injectable()
export class EndPointService {

    getEndPoints(): Promise<EndPoint[]> {
        return Promise.resolve(END_POINTS);
    }

    connect(endPoint: EndPoint): Promise<EndPoint> {
        if (!endPoint.canConnect()) {
            return Promise.resolve(endPoint);
        }

        endPoint.status = EndPoint.CONNECTING;
        return new Promise((resolve, reject) => setTimeout(() => {
            endPoint.status = this.getRandomStatus();
            resolve(endPoint);
        }, 1000));
    }

    disconnect(endPoint: EndPoint): Promise<EndPoint> {
        if (!endPoint.canConnect()) {
            return Promise.resolve(endPoint);
        }

        endPoint.status = EndPoint.DISCONNECTING;
        return new Promise((resolve, reject) => setTimeout(() => {
            endPoint.status = EndPoint.DISCONNECTED;
            resolve(endPoint);
        }, 1000));
    }

    private getRandomStatus(): string {
        let random = Math.floor(Math.random() * 2);
        return random == 0 ? EndPoint.CONNECTED : EndPoint.UNRESPONSIVE;
    }
}