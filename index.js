
import { NativeModules } from 'react-native';

const { RNAudioTrack } = NativeModules;

const AudioPlay = {};


AudioPlay.init = options => RNAudioTrack.init(options);
AudioPlay.start = () => RNAudioTrack.start();
AudioPlay.stop = () => RNAudioTrack.stop();
AudioPlay.write = base64 => RNAudioTrack.write(base64);


export default AudioPlay;
