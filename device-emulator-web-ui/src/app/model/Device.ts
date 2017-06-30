export class Device {

    public static get STARTED(): string { return "STARTED" };
    public static get STOPPED(): string { return "STOPPED" };

    constructor(public name: string,
                public state: string) {
    }

    isStarted(): boolean {
        return this.state == Device.STARTED;
    }
}