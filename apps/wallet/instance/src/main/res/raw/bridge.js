(() => {
    if (!window.tonkeeper) {
        window.rnPromises = {};
        window.rnEventListeners = [];
        window.invokeRnFunc = (name, args, resolve, reject) => {
            const invocationId = btoa(Math.random()).substring(0, 12);
            const timeoutMs = ${timeout};
            const timeoutId = timeoutMs ? setTimeout(() => reject(new Error(\`bridge timeout for function with name: \${name}\`)), timeoutMs) : null;
            window.rnPromises[invocationId] = { resolve, reject, timeoutId }
            window.ReactNativeWebView.postMessage(JSON.stringify({
                type: 'invokeRnFunc',
                invocationId: invocationId,
                name,
                args,
            }));
        };
        window.addEventListener('message', ({ data }) => {
            try {
                const message = JSON.parse(data);
                if (message.type === 'functionResponse') {
                    const promise = window.rnPromises[message.invocationId];
                    if (!promise) {
                        return;
                    }
                    if (promise.timeoutId) {
                        clearTimeout(promise.timeoutId);
                    }
                    if (message.status === 'fulfilled') {
                        promise.resolve(message.data);
                    } else {
                        promise.reject(new Error(message.data));
                    }
                    delete window.rnPromises[message.invocationId];
                } else if (message.type === 'event') {
                    window.rnEventListeners.forEach((listener) => listener(message.event));
                }
            } catch {}
        });
    }
    const listen = (cb) => {
        window.rnEventListeners.push(cb);
        return () => {
            const index = window.rnEventListeners.indexOf(cb);
            if (index > -1) {
                window.rnEventListeners.splice(index, 1);
            }
        };
    };
    window.tonkeeper = {
        tonconnect: Object.assign(
            ${JSON.stringify(obj)},
            {${funcs}},
            { listen },
        ),
    };
})();





