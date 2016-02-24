package com.netflix.imflibrary.imp_validation.cpl;

import com.netflix.imflibrary.IMFErrorLogger;
import com.netflix.imflibrary.IMFErrorLoggerImpl;
import com.netflix.imflibrary.exceptions.IMFException;
import com.netflix.imflibrary.st2067_2.CompositionPlaylist;
import com.netflix.imflibrary.utils.FileByteRangeProvider;
import com.netflix.imflibrary.utils.ResourceByteRangeProvider;
import com.netflix.imflibrary.utils.UUIDHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smpte_ra.schemas.st2067_2_2013.EssenceDescriptorBaseType;
import org.smpte_ra.schemas.st2067_2_2013.TrackFileResourceType;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.annotation.Nonnull;
import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * This class is an interface to a CompositionPlaylist object model providing responses to useful queries about a CPL.
 */
public final class CompositionPlaylistHelper {

    private static final Logger logger = LoggerFactory.getLogger(CompositionPlaylistHelper.class);

    /**
     * A stateless helper method to retrieve the VirtualTracks referenced from within a CompositionPlaylist.
     * @param cplXMLFile - File handle to a CompositionPlaylist XML document.
     * @return A list of VirtualTracks in the CompositionPlaylist.
     * @throws IOException - any I/O related error is exposed through an IOException.
     * @throws IMFException - any non compliant CPL documents will be signalled through an IMFException
     * @throws SAXException - exposes any issues with instantiating a {@link javax.xml.validation.Schema Schema} object
     * @throws JAXBException - any issues in serializing the XML document using JAXB are exposed through a JAXBException
     * @throws URISyntaxException exposes any issues instantiating a {@link java.net.URI URI} object
     */
    @Nonnull
    public static List<CompositionPlaylist.VirtualTrack> getVirtualTracks(@Nonnull File cplXMLFile) throws IOException, IMFException, SAXException, JAXBException, URISyntaxException {
        Map<UUID, CompositionPlaylist.VirtualTrack> virtualTrackMap = CompositionPlaylistHelper.getCompositionPlaylistObjectModel(cplXMLFile).getVirtualTrackMap();
        return new ArrayList<CompositionPlaylist.VirtualTrack>(virtualTrackMap.values());
    }

    /**
     * A stateless helper method to retrieve the VirtualTracks referenced from a CompositionPlaylistRecord.
     * @param compositionPlaylistRecord - A compositionPlaylistRecord object corresponding to the CompositionPlaylist.
     * @return A list of VirtualTracks in the CompositionPlaylist.
     * @throws IOException - any I/O related error is exposed through an IOException.
     * @throws IMFException - any non compliant CPL documents will be signalled through an IMFException
     * @throws SAXException - exposes any issues with instantiating a {@link javax.xml.validation.Schema Schema} object
     * @throws JAXBException - any issues in serializing the XML document using JAXB are exposed through a JAXBException
     * @throws URISyntaxException exposes any issues instantiating a {@link java.net.URI URI} object
     */
    @Nonnull
    public static List<CompositionPlaylist.VirtualTrack> getVirtualTracks(@Nonnull CompositionPlaylistRecord compositionPlaylistRecord) throws IOException, IMFException, SAXException, JAXBException, URISyntaxException {
        Map<UUID, CompositionPlaylist.VirtualTrack> virtualTrackMap = compositionPlaylistRecord.getCompositionPlaylist().getVirtualTrackMap();
        return new ArrayList<>(virtualTrackMap.values());
    }

    /**
     * A stateless helper method to retrieve the UUIDs of the Track files referenced by a Virtual track within a CompositionPlaylist.
     * @param virtualTrack - object model of an IMF virtual track {@link com.netflix.imflibrary.st2067_2.CompositionPlaylist.VirtualTrack}
     * @return A list of TrackFileResourceType objects corresponding to the virtual track in the CompositionPlaylist.
     * @throws IOException - any I/O related error is exposed through an IOException.
     * @throws IMFException - any non compliant CPL documents will be signalled through an IMFException
     * @throws SAXException - exposes any issues with instantiating a {@link javax.xml.validation.Schema Schema} object
     * @throws JAXBException - any issues in serializing the XML document using JAXB are exposed through a JAXBException
     * @throws URISyntaxException exposes any issues instantiating a {@link java.net.URI URI} object
     */
    @Nonnull
    public static List<ResourceIdTuple> getVirtualTrackResourceIDs(@Nonnull CompositionPlaylist.VirtualTrack virtualTrack) throws IOException, IMFException, SAXException, JAXBException, URISyntaxException {

        List<TrackFileResourceType> resourceList = virtualTrack.getResourceList();
        List<ResourceIdTuple> virtualTrackResourceIDs = new ArrayList<>();
        if(resourceList != null
                && resourceList.size() > 0) {
            for (TrackFileResourceType trackFileResourceType : resourceList) {
                virtualTrackResourceIDs.add(new ResourceIdTuple(UUIDHelper.fromUUIDAsURNStringToUUID(trackFileResourceType.getTrackFileId())
                                                                , UUIDHelper.fromUUIDAsURNStringToUUID(trackFileResourceType.getSourceEncoding())));
            }
        }
        return virtualTrackResourceIDs;
    }

    /**
     * This method recursively constructs an object model of a DOM node using a Map.
     * @param nodes a list of DOM nodes whose object model needs to be constructed.
     * @return map containing key value pairs as strings for every element in the DOM node.
     */
    @Nonnull
    public static Map<String, Map<String, String>> getDOMNodeAsAMap(@Nonnull List<Node> nodes){
        Map<String, Map<String, String>>nodeMap = new LinkedHashMap<>();

        return nodeMap;
    }

    /**
     * This method recursively constructs an object model of a DOM node using a Map.
     * @param node the DOM node whose object model needs to be constructed.
     * @return map containing key value pairs as strings for every element in the DOM node.
     */
    @Nonnull
    public static Map<String, String> getDOMNodeAsAMap(@Nonnull Node node){
        Map<String, String>nodeMap = new LinkedHashMap<>();

        return nodeMap;
    }

    private static CompositionPlaylist getCompositionPlaylistObjectModel(@Nonnull File cplXMLFile) throws IOException, IMFException, SAXException, JAXBException, URISyntaxException{
        if(CompositionPlaylist.isCompositionPlaylist(cplXMLFile)){
            IMFErrorLogger imfErrorLogger = new IMFErrorLoggerImpl();
            CompositionPlaylist compositionPlaylist = new CompositionPlaylist(cplXMLFile, imfErrorLogger);
            return compositionPlaylist;
        }
        else{
            throw new IMFException(String.format("CPL document is not compliant with the supported CPL schemas"));
        }
    }

    /**
     * This class is a representation of a Resource SourceEncoding element and trackFileId tuple.
     */
    public static final class ResourceIdTuple{
        private final UUID trackFileId;
        private final UUID sourceEncoding;

        private ResourceIdTuple(UUID trackFileId, UUID sourceEncoding){
            this.trackFileId = trackFileId;
            this.sourceEncoding = sourceEncoding;
        }

        /**
         * A getter for the trackFileId referenced by the resource corresponding to this ResourceIdTuple
         * @return the trackFileId associated with this ResourceIdTuple
         */
        public UUID getTrackFileId(){
            return this.trackFileId;
        }

        /**
         * A getter for the source encoding element referenced by the resource corresponding to this ResourceIdTuple
         * @return the source encoding element associated with this ResourceIdTuple
         */
        public UUID getSourceEncoding(){
            return this.sourceEncoding;
        }
    }

    private static String usage()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Usage:%n"));
        sb.append(String.format("%s <inputFilePath>%n", CompositionPlaylistHelper.class.getName()));
        return sb.toString();
    }

    public static void main(String[] args) throws Exception {

        if (args.length != 1)
        {
            logger.error(usage());
            throw new IllegalArgumentException("Invalid parameters");
        }

        File inputFile = new File(args[0]);

        logger.info(String.format("File Name is %s", inputFile.getName()));

        try
        {
            CompositionPlaylist compositionPlaylist = CompositionPlaylistHelper.getCompositionPlaylistObjectModel(inputFile);
            List<CompositionPlaylist.VirtualTrack> virtualTracks = CompositionPlaylistHelper.getVirtualTracks(inputFile);

            for(CompositionPlaylist.VirtualTrack virtualTrack : virtualTracks){
                List<TrackFileResourceType> resourceList = virtualTrack.getResourceList();
                if(resourceList.size() == 0){
                    throw new Exception(String.format("CPL file has a VirtualTrack with no resources which is invalid"));
                }
            }

            for(EssenceDescriptorBaseType essenceDescriptorBaseType : compositionPlaylist.getCompositionPlaylistType().getEssenceDescriptorList().getEssenceDescriptor()){
                for(Object object : essenceDescriptorBaseType.getAny()){
                    Node node = (Node)object;

                }
            }

            System.out.println(String.format("De-serialized composition playlist : %s", compositionPlaylist.toString()));
        }
        catch(Exception e)
        {
            throw new Exception(e);
        }
    }
}
